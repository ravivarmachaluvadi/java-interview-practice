/*
 * =====================================================================
 *  WorkFlowExecutor - key-affinity ordered execution   Spring @Component | header-only
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   A Spring component giving ordered execution per key: tasks sharing a key (an
 *   order number) run one at a time and in submission order, while different keys
 *   run in parallel. It is the concurrency backbone of the grouped stream processor.
 *
 *   Moving parts:
 *     init()      @PostConstruct - N single-thread pools, each on a LimitQueue.
 *     submitTask  wraps the Procedure in a Runnable that unlinks the key in a
 *                 finally; invents a random key when the caller supplies none.
 *     addTask     per-key lock: reuse or pick an executor, bump in-flight count, run.
 *     unlink      decrement; at zero drop the key from jobMap and activeOrderMap.
 *     @PreDestroy drains every pool from parallel threads.
 *
 * WHAT TO NOTICE
 *   - Ordering holds only while a key's in-flight count stays above zero. At zero
 *     the mapping is dropped, so the next task for that key may land on a different
 *     executor. Affinity for overlapping work, not a permanent per-key serialiser.
 *
 *   - synchronized (orderNumber.intern()) is the sharp edge. intern() returns a
 *     JVM-wide String, so this monitor is shared with any other code locking the
 *     same literal, and interning caller data grows the string table. The usual fix
 *     is a ConcurrentHashMap of dedicated lock objects.
 *
 *   - handleExecService() picks the least-loaded queue. Only when EVERY queue sits
 *     at queueSize does it fall back to round-robin via findIndexForExecService().
 *
 *   - Latent NPE: handleMapForUnlinkTask() unboxes activeOrderMap.get(key) before
 *     any null check, so a missing key throws inside the try, the catch swallows it,
 *     and the jobMap entry is left behind - a slow map leak on error paths.
 *
 *   - Shutdown order is inverted: awaitTermination(timeout) runs BEFORE shutdown().
 *     On a live pool that blocks for the whole shutdownTimeout and returns false,
 *     then shutdownNow() kills in-flight work. Correct order is shutdown(), then
 *     awaitTermination(), then shutdownNow().
 *
 *   - org.junit Assertions.assertNotNull in production code drags a test dependency
 *     into main and throws AssertionFailedError, not an argument exception.
 *
 *   - Because the queue is a LimitQueue, a full queue BLOCKS the submitting thread
 *     rather than rejecting the task; the original javadoc claiming rejection with
 *     an exception was wrong. TaskType.UNLINK is declared but never handled.
 *
 *   - Tuning fields are @Value-injected, so they are still zero at construction
 *     time - that is why the pools are built in @PostConstruct, not a constructor.
 */
package com.tgt.gom.federator.grouped_processor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tgt.gom.constants.OmsConstants;
import com.tgt.gom.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WorkFlowExecutor {

    @Value("${workflowExecutor.count:6}")
    private int groupedProcessorCount;

    @Value("${workflowExecutor.enabled:false}")
    private boolean isEnabled;

    @Value("${workflowExecutor.queueSize:40}")
    private int queueSize;

    @Value("${workflowExecutor.shutdownTimeout:60}")
    private int shutdownTimeout;

    private final List<ThreadPoolExecutor> executorServices = new ArrayList<>();

    private static final String LOG_OP = "Concurrent_Ordered_Processor";

    private final Map<String, ThreadPoolExecutor> jobMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> activeOrderMap = new ConcurrentHashMap<>();
    private final AtomicInteger execServiceIndex = new AtomicInteger();


    /**
     * Intialize the ThreadPoolExecutor service with defined pool and queue config
     */
    @PostConstruct
    public void init() {
        if (isEnabled) {
            for (int i = 0; i < groupedProcessorCount; i++) {
                ThreadFactory namedThreadFactory =
                        new ThreadFactoryBuilder().setNameFormat("es_snapshot_" + i + "-thread-%d").build();
                executorServices.add(new ThreadPoolExecutor(1, 1, 1L,
                        TimeUnit.MILLISECONDS, new LimitQueue<>(queueSize), namedThreadFactory));
            }
        }
    }

    /**
     * Shutdown hook to gracefully terminate the executor services
     */
    @PreDestroy
    public void awaitTermination() {
        List<Thread> parallelShutdownInvokers = executorServices.stream()
                .map(threadPoolExecutor -> {
                    Runnable runnableTask = () -> {
                        try {
                            log.info(OmsConstants.LOGGER_OK_PATTERN, LOG_OP,
                                    "Shutdown attempted, Executor service pool:" + threadPoolExecutor.toString());
                            threadPoolExecutor.awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
                            threadPoolExecutor.shutdown();
                        } catch (InterruptedException e) {
                            log.info(OmsConstants.LOGGER_KO_EXCEPTION_PATTERN, LOG_OP,
                                    "Shutdown interrupted" + threadPoolExecutor.toString(), e.getStackTrace());
                        } finally {
                            if (!threadPoolExecutor.isTerminated()) {
                                log.warn(OmsConstants.LOGGER_OK_PATTERN, LOG_OP,
                                        "Executor Service Pool not terminated " + threadPoolExecutor.toString());
                            }
                            threadPoolExecutor.shutdownNow();
                        }
                    };
                    return new Thread(runnableTask);
                }).collect(Collectors.toList());
        parallelShutdownInvokers.forEach(Thread::start);
        parallelShutdownInvokers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error(OmsConstants.LOGGER_OK_PATTERN, LOG_OP, MessageFormat
                        .format("Error in thread join, {0}", (Object) e.getStackTrace()));
            }
        });
    }

    /**
     * Submit task to the computed to WorkFlow executor
     *
     * @param toExecute   procedure to process/execute
     * @param payload     payload required for the procedure
     * @param key orderNumber/key for the executor
     */
    public void submitTask(final Procedure toExecute, final String payload, final String key) {
        final String internalKey = StringUtils.isEmpty(key)
                ? GeneralUtil.generateRandomNumber(1000000000, 100000000)
                : key;

        Assertions.assertNotNull(internalKey);
        Runnable runnableTask = () -> {
            try {
                toExecute.invoke(payload);
            } finally {
                handleUnlinkTask(internalKey);
            }
        };
        handleAddTask(runnableTask, internalKey, TaskType.ADD_TASK);
    }

    /**
     * Handle the processor (callback Task) based on task type
     * ADD_TASK : Find the optimal ThreadPoolExecutor and process the payload wrt orderNumber
     *
     * @param runnableTask task to execute
     * @param orderNumber  orderNumber for the task
     * @param taskType     Add or Unlink the order order number to ThreadPoolExecutor
     */
    private void handleAddTask(final Runnable runnableTask, final String orderNumber, final TaskType taskType) {
        String stringPoolOrderNumber = orderNumber.intern();
        synchronized (stringPoolOrderNumber) {
            try {
                ThreadPoolExecutor executorService = jobMap.get(stringPoolOrderNumber);
                if (executorService == null) {
                    executorService = handleExecService(taskType);
                }
                handleMapForAddTask(stringPoolOrderNumber, executorService);
                executorService.execute(runnableTask);
            } catch (Exception e) {
                log.error(OmsConstants.LOGGER_OK_PATTERN, LOG_OP, MessageFormat
                        .format("Error in executing {0} for order {1}, {2}", taskType, orderNumber, e.getStackTrace()));
                //TODO: Check if clearing complete map cache is required
                handleMapForUnlinkTask(stringPoolOrderNumber);
            }
        }
    }

    /**
     * Unlink the order number from Job Maps
     *
     * @param orderNumber order Number
     */
    private void handleUnlinkTask(final String orderNumber) {
        try {
            handleMapForUnlinkTask(orderNumber);
        } catch (Exception e) {
            log.error(OmsConstants.LOGGER_OK_PATTERN, LOG_OP, MessageFormat
                    .format("Error in Un-linking order {0}, {1}", orderNumber, e.getStackTrace()));
            //TODO: Check if clearing complete map cache is required
            handleMapForUnlinkTask(orderNumber);
        }
    }

    /**
     * Unlink the ThreadPoolExecutor from OrderNumber if required
     *
     * @param orderNumber orderNumber to unlink
     */
    private void handleMapForUnlinkTask(final String orderNumber) {
        Integer orderNumberCount = 0;
        try {
            orderNumberCount = activeOrderMap.get(orderNumber);
            //Dont unlink ThreadPoolExecutor if same order is present in the queue for further processing
            if (orderNumberCount > 1) {
                activeOrderMap.put(orderNumber, orderNumberCount - 1);
                return;
            }
            activeOrderMap.remove(orderNumber);
            jobMap.remove(orderNumber);
        } catch (Exception e) {
            log.error(OmsConstants.LOGGER_OK_PATTERN, LOG_OP, MessageFormat
                    .format("Error in UNLINK for order {0}, {1}", orderNumber, orderNumberCount));
        }
    }

    /**
     * Add/Link ThreadPoolExecutor to a order number
     *
     * @param orderNumber     orderNumber to link
     * @param executorService threadPoolExecutor to be linked for processing
     */
    private void handleMapForAddTask(final String orderNumber, final ThreadPoolExecutor executorService) {
        jobMap.put(orderNumber, executorService);
        Integer orderNumberCount = activeOrderMap.get(orderNumber);
        if (orderNumberCount == null || orderNumberCount == 0) {
            orderNumberCount = 1;
        } else {
            orderNumberCount = orderNumberCount + 1;
        }
        activeOrderMap.put(orderNumber, orderNumberCount);
    }

    /**
     * Find the best ThreadPoolExecutor if order number is not linked to any Executor
     *
     * @param taskType ADD_TASK to link
     * @return Computed ThreadPoolExecutor
     */
    private ThreadPoolExecutor handleExecService(final TaskType taskType) {
        ThreadPoolExecutor threadPoolExecutor = null;
        if (TaskType.ADD_TASK.equals(taskType)) {
            Optional<ThreadPoolExecutor> minQueueService = executorServices.stream()
                    .min(Comparator.comparingInt(o -> o.getQueue().size()));

            long maxQueueExecService = executorServices.stream()
                    .filter(tPExecutor -> tPExecutor.getQueue().size() == queueSize)
                    .count();

            if (maxQueueExecService == groupedProcessorCount) {
                int index = findIndexForExecService();
                threadPoolExecutor = executorServices.get(index);
            } else {
                threadPoolExecutor = minQueueService.orElseGet(() -> {
                    log.info(OmsConstants.LOGGER_OK_PATTERN, LOG_OP, "Defaulting to 0th Executor Service");
                    return executorServices.get(0);
                });
            }
        }
        return threadPoolExecutor;
    }

    /**
     * If all the ThreadPoolExecutor Queues are Full, follow a round robin approach to fecth the ThreadPoolExecutor
     * for load balancing
     *
     * @return find the next index of ThreadPoolExecutor
     */
    private int findIndexForExecService() {
        int index = execServiceIndex.getAndIncrement();
        if (index >= executorServices.size()) {
            execServiceIndex.set(0);
            return 0;
        }
        return index;
    }

    /**
     * Fetch the status of Processor
     *
     * @return true/false based on enabled/disabled
     */
    public boolean isProcessorEnabled() {
        return isEnabled;
    }

    public static void main(String[] args) throws Exception {
        WorkFlowExecutor executor = new WorkFlowExecutor();
        // manually set fields that would normally come from Spring
        java.lang.reflect.Field enabledField = WorkFlowExecutor.class.getDeclaredField("isEnabled");
        enabledField.setAccessible(true);
        enabledField.setBoolean(executor, true);
        java.lang.reflect.Field countField = WorkFlowExecutor.class.getDeclaredField("groupedProcessorCount");
        countField.setAccessible(true);
        countField.setInt(executor, 2);
        java.lang.reflect.Field queueField = WorkFlowExecutor.class.getDeclaredField("queueSize");
        queueField.setAccessible(true);
        queueField.setInt(executor, 10);

        // initialize executor services
        executor.init();

        System.out.println("=== Input ===");
        String payload1 = "Task A Payload";
        String payload2 = "Task B Payload";
        String key = "order123";

        System.out.println("Submitting Task 1 with key: " + key);
        executor.submitTask(payload -> System.out.println("[PROC] Executing: " + payload), payload1, key);

        System.out.println("Submitting Task 2 with same key to test ordering");
        executor.submitTask(payload -> System.out.println("[PROC] Executing: " + payload), payload2, key);

        // give some time for tasks to complete
        Thread.sleep(2000);

        System.out.println("\n=== Output ===");
        // output is printed by the procedure callbacks above

        // shutdown executors gracefully
        executor.awaitTermination();
    }
}

/**
 * Type of the task for execution
 */
enum TaskType {
    ADD_TASK, UNLINK
}

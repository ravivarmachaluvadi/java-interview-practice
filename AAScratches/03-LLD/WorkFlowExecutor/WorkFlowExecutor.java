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
}

/**
 * Type of the task for execution
 */
enum TaskType {
    ADD_TASK, UNLINK
}

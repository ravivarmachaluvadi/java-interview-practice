/*
 * =====================================================================
 *  JDK Dynamic Proxy (InvocationHandler)          Java core | Medium   MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   How to create, at runtime, an object that implements one or more interfaces without
 *   writing a class for it. Every call on that object is funnelled into a single method,
 *   InvocationHandler.invoke, where you can log, time, retry, or delegate. This is the
 *   machinery behind Spring AOP, @Transactional, Mockito mocks and MyBatis mappers.
 *
 * WHAT YOU WILL SEE
 *   case 1: a call to performAction() is wrapped with "before" and "after" work and then
 *           forwarded to the real OriginalClass instance.
 *   case 2: a call to additionalAction() - a method the real object does NOT implement -
 *           is served entirely by the handler, because the proxy implements that interface
 *           too. Blindly forwarding it would blow up (see GOTCHAS).
 *   case 3: identity checks - the proxy is an instance of both interfaces and its class
 *           was generated at runtime, so it is not OriginalClass.
 *
 * HOW IT WORKS
 *   1. Proxy.newProxyInstance(loader, interfaces, handler) generates a class such as
 *      $Proxy0 that implements every interface in the array.
 *   2. Each generated method body is the same: package the arguments and call
 *      handler.invoke(proxyItself, theMethodObject, args).
 *   3. The handler decides what to do. Here it appends to a call log, then either
 *      reflects the call onto the wrapped target or answers it itself.
 *   4. Whatever invoke returns is returned to the caller, so the return type must match.
 *
 * KEY INSIGHT
 *   The proxy is typed by INTERFACES, not by the target object. The interface list and
 *   the target are independent: the proxy can expose methods the target never had, and it
 *   can hide that a target exists at all. That decoupling is why one handler (a
 *   transaction interceptor, say) can wrap any bean in the container.
 *
 * GOTCHAS
 *   - The target class is irrelevant to the type of the proxy. Forwarding additionalAction
 *     with method.invoke(target, args) throws IllegalArgumentException ("object is not an
 *     instance of declaring class"), which is why the handler checks first.
 *   - JDK proxies need an interface. To proxy a concrete class you need CGLIB or
 *     ByteBuddy, which subclass it - that is why Spring switches proxy modes.
 *   - equals, hashCode and toString also reach invoke; forgetting them gives a proxy whose
 *     toString recurses or whose equals is broken.
 *   - A self-call inside the target (this.other()) does NOT go through the proxy. That is
 *     the classic "my @Transactional method is ignored" bug.
 *
 * INTERVIEW FOLLOW-UPS
 *   - JDK proxy vs CGLIB: interfaces vs subclassing, final methods, no-arg constructors.
 *   - How would you add retry-with-backoff to every method of a service using this?
 *   - Why does calling a @Transactional method from another method of the same bean fail?
 *   - What is the cost of reflection here, and how does Spring reduce it?
 *
 * RUN
 *   main() runs 3 cases (forwarded call, handler-only call, identity checks) and prints
 *   actual vs expected.
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

// Step 1: the interfaces the proxy will implement.
interface OriginalInterface {
    void performAction();
}

interface AdditionalInterface {
    void additionalAction();
}

// Step 2: the real object. Note it implements ONLY OriginalInterface.
class OriginalClass implements OriginalInterface {
    public void performAction() {
        System.out.println("  target: performing the original action...");
    }
}

// Step 3: the one method every proxy call is funnelled into.
class ProxyHandler implements InvocationHandler {
    private final Object originalObject;
    private final List<String> callLog = new ArrayList<>();

    public ProxyHandler(Object originalObject) {
        this.originalObject = originalObject;
    }

    public List<String> getCallLog() {
        return List.copyOf(callLog);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object's own methods must never be forwarded blindly or toString can recurse.
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        callLog.add("before:" + method.getName());
        System.out.println("  handler: before " + method.getName());

        Object result;
        // The proxy may expose interfaces the target does not implement, so check first.
        if (method.getDeclaringClass().isInstance(originalObject)) {
            result = method.invoke(originalObject, args);       // forward to the real object
        } else {
            System.out.println("  handler: target has no " + method.getName()
                    + "(), serving it here");
            callLog.add("handled-by-proxy:" + method.getName());
            result = null;                                      // void method, so null is fine
        }

        System.out.println("  handler: after " + method.getName());
        callLog.add("after:" + method.getName());
        return result;
    }
}

// Step 4: build the proxy and exercise it.
class DynamicProxyDemo {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        OriginalInterface originalObject = new OriginalClass();
        ProxyHandler handler = new ProxyHandler(originalObject);

        Object proxy = Proxy.newProxyInstance(
                OriginalInterface.class.getClassLoader(),
                new Class<?>[]{OriginalInterface.class, AdditionalInterface.class},
                handler);

        // case 1: a method the target really implements - forwarded.
        System.out.println("case 1: performAction() on the proxy");
        ((OriginalInterface) proxy).performAction();
        print("case 1 log", handler.getCallLog(),
                "[before:performAction, after:performAction]");

        // case 2: a method only the PROXY implements - answered by the handler.
        System.out.println("case 2: additionalAction() on the proxy");
        ((AdditionalInterface) proxy).additionalAction();
        print("case 2 log", handler.getCallLog(),
                "[before:performAction, after:performAction, before:additionalAction, "
                        + "handled-by-proxy:additionalAction, after:additionalAction]");

        // case 3: what the proxy actually is.
        print("case 3 isProxyClass", Proxy.isProxyClass(proxy.getClass()), true);
        print("case 3 is OriginalInterface", proxy instanceof OriginalInterface, true);
        print("case 3 is AdditionalInterface", proxy instanceof AdditionalInterface, true);
        print("case 3 is OriginalClass", proxy instanceof OriginalClass, false);
        print("case 3 handler round-trip", Proxy.getInvocationHandler(proxy) == handler, true);
    }
}

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// Step 1: Define the interfaces
interface OriginalInterface {
    void performAction();
}

interface AdditionalInterface {
    void additionalAction();
}

// Step 2: Implement the original class
class OriginalClass implements OriginalInterface {
    public void performAction() {
        System.out.println("Performing the original action...");
    }
}

// Step 3: Implement the invocation handler
class ProxyHandler implements InvocationHandler {
    private Object originalObject;

    public ProxyHandler(Object originalObject) {
        this.originalObject = originalObject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Additional functionality before calling the original object
        System.out.println("Performing additional steps before calling the original object...");

        // Invoke the method on the original object
        Object result = method.invoke(originalObject, args);

        // Additional functionality after calling the original object
        System.out.println("Performing additional steps after calling the original object...");

        return result;
    }
}

// Step 4: Main code
class DynamicProxyDemo {
    public static void main(String[] args) {
        OriginalInterface originalObject = new OriginalClass();

        OriginalInterface proxyObject = (OriginalInterface) Proxy.newProxyInstance(
                OriginalInterface.class.getClassLoader(),
                new Class[]{OriginalInterface.class, AdditionalInterface.class},
                new ProxyHandler(originalObject)
        );
        // Call the methods on the proxy object
        proxyObject.performAction();
    }
}

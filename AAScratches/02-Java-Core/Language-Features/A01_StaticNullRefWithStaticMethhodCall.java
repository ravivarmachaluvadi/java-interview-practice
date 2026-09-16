class StaticNullRefWithStaticMethhodCall {
    public static void main(String[] args) {
        StaticNullRef object = null;
        // no error or exception will occur method will be invoked
        object.staticMethod();
    }
}

class StaticNullRef {

    public static void staticMethod() {
        System.out.println("staticMethod with null object reference");
    }
}

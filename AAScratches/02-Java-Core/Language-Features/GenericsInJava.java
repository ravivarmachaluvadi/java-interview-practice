class NonGenericClass {
    static <T> void genericMethod(T t1) {
        T t2 = t1;

        System.out.println(t2);
    }
}

public class GenericsInJava {
    public static void main(String[] args) {
        NonGenericClass.genericMethod(new Integer(123));     //Passing Integer type as an argument

        NonGenericClass.genericMethod("I am string");        //Passing String type as an argument

        NonGenericClass.genericMethod(new Double(25.89));    //Passing Double type as an argument
    }
}

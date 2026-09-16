public class PM {
    public static void main(String[] args) {
        System.out.println(format(new Object()));
        System.out.println(format( Integer.valueOf(12)));
        System.out.println(format( Double.valueOf(12)));
        System.out.println(format( Long.valueOf(12)));
        System.out.println(format( "String"));
    }

    static String format(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case String s  -> String.format("String '%s'", s);
            case null      -> "null value";
            default        -> obj.toString();
        };
    }
}
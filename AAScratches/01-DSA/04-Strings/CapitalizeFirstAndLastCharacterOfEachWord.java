class CapitalizeFirstAndLastCharacterOfEachWord {

    public String setCapital(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.setCharAt(sb.length() - 1, Character
                .toUpperCase(sb.charAt(sb.length() - 1)));
        return sb.toString();
    }

    public String capitalizeFirstLast(String s) {
        String arr[] = s.split(" ");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = setCapital(arr[i]);
        }
        return String.join(" ", arr);
    }

    public static void main(String[] args) {
        CapitalizeFirstAndLastCharacterOfEachWord obj = new CapitalizeFirstAndLastCharacterOfEachWord();
        String s = "hello world from java";
        System.out.println(obj.capitalizeFirstLast(s));
    }
}

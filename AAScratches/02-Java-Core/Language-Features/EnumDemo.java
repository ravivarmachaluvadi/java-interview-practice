class EnumDemo {
    public static void main(String[] args) {
        for (MyEnum value : MyEnum.values()) {
            value.m1();
        }
    }
}

enum MyEnum {

    SUNDAY(0) {
        @Override
        void m1() {
            System.out.println("Override by " + MyEnum.SUNDAY + " ");
        }
    },
    MONDAY(1) {
        @Override
        void m1() {
            System.out.println("Override by " + MyEnum.MONDAY);
        }
    };

    final int value;

    abstract void m1();

    MyEnum(int i) {
        this.value = i;
    }
}


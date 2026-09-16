class NumberOfDays {
    public int numberOfDays(int year, int month) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // Check for February and leap year
        if (month == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) {
            return 29;
        }

        return daysInMonth[month - 1];
    }

    public static void main(String[] args) {
        NumberOfDays solution = new NumberOfDays();
        int year = 2020;
        int month = 2;
        System.out.println("Number of days in " + month + "/" + year + ": " + solution.numberOfDays(year, month));
    }
}

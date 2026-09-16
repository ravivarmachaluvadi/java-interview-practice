class StudentGrade {
    public void studentGrade(int marks) {

        // If else ladder
        if (marks >= 90) {
            System.out.print("Grade A");
        } else if (marks >= 70) {
            System.out.print("Grade B");
        } else if (marks >= 50) {
            System.out.print("Grade C");
        } else if (marks >= 35) {
            System.out.print("Grade D");
        } else {
            System.out.print("Fail");
        }
    }

    public static void main(String[] args) {
        StudentGrade obj = new StudentGrade();
        obj.studentGrade(85); // Example usage
    }
}
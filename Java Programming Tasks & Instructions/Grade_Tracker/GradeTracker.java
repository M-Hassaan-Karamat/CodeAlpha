import java.util.*;

class Student {
    private String name;
    private List<Double> grades;

    public Student(String name) {
        this.name = name;
        this.grades = new ArrayList<>();
    }

    public void addGrade(double grade) {
        if (grade >= 0 && grade <= 100) {
            grades.add(grade);
        } else {
            System.out.println("Invalid grade. Please enter a grade between 0 and 100.");
        }
    }

    public double calculateAverage() {
        if (grades.isEmpty()) return 0;
        double sum = 0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    public double getHighestGrade() {
        if (grades.isEmpty()) return 0;
        return Collections.max(grades);
    }

    public double getLowestGrade() {
        if (grades.isEmpty()) return 0;
        return Collections.min(grades);
    }

    public String getName() {
        return name;
    }

    public List<Double> getGrades() {
        return new ArrayList<>(grades);
    }
}

public class GradeTracker {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\nGrade Tracker System");
            System.out.println("1. Add New Student");
            System.out.println("2. Add Grade to Student");
            System.out.println("3. View Student Report");
            System.out.println("4. View Class Summary");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        addNewStudent();
                        break;
                    case 2:
                        addGradeToStudent();
                        break;
                    case 3:
                        viewStudentReport();
                        break;
                    case 4:
                        viewClassSummary();
                        break;
                    case 5:
                        running = false;
                        System.out.println("Exiting Grade Tracker. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        scanner.close();
    }

    private static void addNewStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            students.add(new Student(name));
            System.out.println("Student added successfully!");
        } else {
            System.out.println("Name cannot be empty!");
        }
    }

    private static void addGradeToStudent() {
        if (students.isEmpty()) {
            System.out.println("No students available. Please add a student first.");
            return;
        }

        System.out.println("\nSelect a student:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName());
        }

        try {
            System.out.print("Enter student number: ");
            int studentIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (studentIndex >= 0 && studentIndex < students.size()) {
                System.out.print("Enter grade (0-100): ");
                double grade = Double.parseDouble(scanner.nextLine());
                students.get(studentIndex).addGrade(grade);
                System.out.println("Grade added successfully!");
            } else {
                System.out.println("Invalid student number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void viewStudentReport() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.println("\nSelect a student to view report:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName());
        }

        try {
            System.out.print("Enter student number: ");
            int studentIndex = Integer.parseInt(scanner.nextLine()) - 1;
            
            if (studentIndex >= 0 && studentIndex < students.size()) {
                Student student = students.get(studentIndex);
                System.out.println("\n--- Student Report ---");
                System.out.println("Name: " + student.getName());
                System.out.println("Grades: " + student.getGrades());
                System.out.println("Average: " + String.format("%.2f", student.calculateAverage()));
                System.out.println("Highest Grade: " + student.getHighestGrade());
                System.out.println("Lowest Grade: " + student.getLowestGrade());
                System.out.println("----------------------");
            } else {
                System.out.println("Invalid student number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void viewClassSummary() {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }

        System.out.println("\n--- Class Summary ---");
        System.out.printf("%-20s %-15s %-15s %-15s\n", 
            "Student Name", "Average", "Highest", "Lowest");
        System.out.println("-".repeat(65));
        
        for (Student student : students) {
            System.out.printf("%-20s %-15.2f %-15.2f %-15.2f\n",
                student.getName(),
                student.calculateAverage(),
                student.getHighestGrade(),
                student.getLowestGrade());
        }
        
        // Calculate class statistics
        double classAverage = students.stream()
            .mapToDouble(Student::calculateAverage)
            .average()
            .orElse(0);
            
        double highestInClass = students.stream()
            .mapToDouble(Student::getHighestGrade)
            .max()
            .orElse(0);
            
        double lowestInClass = students.stream()
            .mapToDouble(Student::getLowestGrade)
            .min()
            .orElse(0);
            
        System.out.println("\nClass Statistics:");
        System.out.println("Average Grade: " + String.format("%.2f", classAverage));
        System.out.println("Highest Grade in Class: " + highestInClass);
        System.out.println("Lowest Grade in Class: " + lowestInClass);
        System.out.println("Total Students: " + students.size());
        System.out.println("----------------------");
    }
}

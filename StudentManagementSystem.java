import java.io.*;
import java.util.*;

// ========================= Person (Abstract) =========================
abstract class Person {
    String name;
    String email;

    Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    abstract void displayInfo();
}

// ========================= Student Class =========================
class Student extends Person {
    int rollNo;
    String course;
    double marks;
    String grade;

    Student(int rollNo, String name, String email, String course, double marks) {
        super(name, email);
        this.rollNo = rollNo;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    void inputDetails() {}

    void calculateGrade() {
        if (marks >= 90) grade = "A";
        else if (marks >= 75) grade = "B";
        else if (marks >= 60) grade = "C";
        else grade = "D";
    }

    @Override
    void displayInfo() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + grade);
    }
}

// ========================= Custom Exception =========================
class StudentNotFoundException extends Exception {
    StudentNotFoundException(String msg) {
        super(msg);
    }
}

// ========================= Interface =========================
interface RecordActions {
    void addStudent();
    void deleteStudent() throws StudentNotFoundException;
    void updateStudent() throws StudentNotFoundException;
    void searchStudent() throws StudentNotFoundException;
    void viewAllStudents();
}

// ========================= Loader (Thread) =========================
class Loader implements Runnable {
    @Override
    public void run() {
        System.out.print("Loading");
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
        } catch (Exception e) {}
        System.out.println("\nDone.");
    }
}

// ========================= Student Manager =========================
class StudentManager implements RecordActions {

    Map<Integer, Student> studentMap = new HashMap<>();
    Scanner sc = new Scanner(System.in);

    StudentManager() {
        loadFromFile();
    }

    // -------- Add Student --------
    @Override
    public void addStudent() {
        try {
            System.out.print("Enter Roll No: ");
            int roll = Integer.parseInt(sc.nextLine());

            if (studentMap.containsKey(roll)) {
                System.out.println("Roll number already exists!");
                return;
            }

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Course: ");
            String course = sc.nextLine();

            System.out.print("Enter Marks: ");
            double marks = Double.parseDouble(sc.nextLine());

            if (marks < 0 || marks > 100)
                throw new IllegalArgumentException("Marks must be between 0 and 100");

            Thread t = new Thread(new Loader());
            t.start();
            t.join();

            Student s = new Student(roll, name, email, course, marks);
            studentMap.put(roll, s);
            saveToFile();
            System.out.println("Student added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // -------- View Students --------
    @Override
    public void viewAllStudents() {
        if (studentMap.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (Student s : studentMap.values()) {
            System.out.println("-------------------");
            s.displayInfo();
        }
    }

    // -------- Search Student --------
    @Override
    public void searchStudent() throws StudentNotFoundException {
        System.out.print("Enter name to search: ");
        String name = sc.nextLine().toLowerCase();

        boolean found = false;
        for (Student s : studentMap.values()) {
            if (s.name.toLowerCase().contains(name)) {
                s.displayInfo();
                found = true;
            }
        }

        if (!found) throw new StudentNotFoundException("Student not found.");
    }

    // -------- Delete Student --------
    @Override
    public void deleteStudent() throws StudentNotFoundException {
        System.out.print("Enter name to delete: ");
        String name = sc.nextLine().toLowerCase();

        Integer rollToDelete = null;

        for (Student s : studentMap.values()) {
            if (s.name.toLowerCase().equals(name)) {
                rollToDelete = s.rollNo;
                break;
            }
        }

        if (rollToDelete == null)
            throw new StudentNotFoundException("Student not found.");

        studentMap.remove(rollToDelete);
        saveToFile();
        System.out.println("Student deleted.");
    }

    // -------- Update Student --------
    @Override
    public void updateStudent() throws StudentNotFoundException {
        System.out.print("Enter roll number to update: ");
        int roll = Integer.parseInt(sc.nextLine());

        if (!studentMap.containsKey(roll))
            throw new StudentNotFoundException("Student not found");

        Student s = studentMap.get(roll);

        System.out.print("Enter new email: ");
        s.email = sc.nextLine();

        System.out.print("Enter new marks: ");
        s.marks = Double.parseDouble(sc.nextLine());
        s.calculateGrade();

        saveToFile();
        System.out.println("Record updated.");
    }

    // -------- Sort Students --------
    public void sortByMarks() {
        List<Student> list = new ArrayList<>(studentMap.values());

        list.sort((a, b) -> Double.compare(b.marks, a.marks));

        System.out.println("Sorted List:");
        for (Student s : list) {
            System.out.println("------------------");
            s.displayInfo();
        }
    }

    // -------- File Handling --------
    void saveToFile() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("students.txt"));
            for (Student s : studentMap.values()) {
                bw.write(s.rollNo + "," + s.name + "," + s.email + "," + s.course + "," + s.marks);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Save Error");
        }
    }

    void loadFromFile() {
        try {
            File file = new File("students.txt");
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                int roll = Integer.parseInt(p[0]);
                studentMap.put(roll, new Student(roll, p[1], p[2], p[3], Double.parseDouble(p[4])));
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Load Error");
        }
    }
}

// ========================= Main Class =========================
public class StudentSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager manager = new StudentManager();

        while (true) {
            System.out.println("\n===== Capstone Student Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search by Name");
            System.out.println("4. Delete by Name");
            System.out.println("5. Update Student");
            System.out.println("6. Sort by Marks");
            System.out.println("7. Save and Exit");
            System.out.print("Enter choice: ");

            int ch = Integer.parseInt(sc.nextLine());

            try {
                switch (ch) {
                    case 1 -> manager.addStudent();
                    case 2 -> manager.viewAllStudents();
                    case 3 -> manager.searchStudent();
                    case 4 -> manager.deleteStudent();
                    case 5 -> manager.updateStudent();
                    case 6 -> manager.sortByMarks();
                    case 7 -> {
                        manager.saveToFile();
                        System.out.println("Saved and exiting.");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}


package com.attendance;

import java.time.LocalDate;
import java.util.Scanner;

/** Console menu - the starting point of the program. */
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        StudentDAO studentDAO = new StudentDAO();

        while (true) {
            System.out.println("\n===== FACE ATTENDANCE SYSTEM =====");
            System.out.println("1. Register new student (capture face)");
            System.out.println("2. Train model");
            System.out.println("3. Start attendance");
            System.out.println("4. View today's report");
            System.out.println("5. View report by date");
            System.out.println("6. Open teacher dashboard");
            System.out.println("7. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Student name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Roll number: ");
                    String roll = sc.nextLine().trim();
                    int id = studentDAO.addStudent(name, roll);
                    if (id != -1) {
                        System.out.println("Student added with id " + id + ". Opening camera...");
                        new FaceRegistration().capture(id);
                        System.out.println("Now choose option 2 to train the model.");
                    }
                }
                case "2" -> new FaceTrainer().train();
                case "3" -> new AttendanceMarker().start();
                case "4" -> new AttendanceDAO().printReport(LocalDate.now().toString());
                case "5" -> {
                    System.out.print("Enter date (yyyy-MM-dd): ");
                    new AttendanceDAO().printReport(sc.nextLine().trim());
                }
                case "6" -> DashboardWindow.open();
                case "7" -> {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
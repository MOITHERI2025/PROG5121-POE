package prog5121_poe;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();

        System.out.println("=== REGISTRATION ===");
        System.out.print("First Name: ");
        String fName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lName = scanner.nextLine();
        System.out.print("Username (underscore, max 5 chars): ");
        String username = scanner.nextLine();
        System.out.print("Password (8+ chars, capital, number, special char): ");
        String password = scanner.nextLine();
        System.out.print("Cell number (e.g. +27831234567): ");
        String cell = scanner.nextLine();

        String regMsg = loginSystem.registerUser(username, password, cell, fName, lName);
        System.out.println(regMsg);

        if (regMsg.contains("registered successfully")) {
            System.out.println("\n=== LOGIN ===");
            System.out.print("Username: ");
            String loginUser = scanner.nextLine();
            System.out.print("Password: ");
            String loginPass = scanner.nextLine();

            boolean isLoggedIn = loginSystem.loginUser(loginUser, loginPass);
            System.out.println(loginSystem.returnLoginStatus(isLoggedIn));
        }
    }
}
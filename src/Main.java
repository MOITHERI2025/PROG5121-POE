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

        boolean isLoggedIn = false;
        if (regMsg.contains("registered successfully")) {
            System.out.println("\n=== LOGIN ===");
            System.out.print("Username: ");
            String loginUser = scanner.nextLine();
            System.out.print("Password: ");
            String loginPass = scanner.nextLine();

            isLoggedIn = loginSystem.loginUser(loginUser, loginPass);
            System.out.println(loginSystem.returnLoginStatus(isLoggedIn));
        }

        // Only allow messaging if login succeeded
        if (isLoggedIn) {
            System.out.println("\nWelcome to QuickChat.");

            System.out.print("How many messages would you like to send this session? ");
            int numMessages = Integer.parseInt(scanner.nextLine());

            boolean quit = false;
            int messagesEnteredSoFar = 0;

            while (!quit) {
                System.out.println("\n1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        if (messagesEnteredSoFar >= numMessages) {
                            System.out.println("You've reached your message limit for this session.");
                            break;
                        }
                        for (int i = messagesEnteredSoFar; i < numMessages; i++) {
                            System.out.print("Recipient cell number (e.g. +27831234567): ");
                            String recipient = scanner.nextLine();
                            System.out.print("Message text: ");
                            String text = scanner.nextLine();

                            Message msg = new Message(recipient, text, i);

                            System.out.println(msg.checkRecipientCell());
                            System.out.println(msg.checkMessageLength());
                            msg.createMessageHash();
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Message ID generated: " + msg.getMessageID());

                            System.out.println("1) Send  2) Disregard  3) Store");
                            System.out.print("Choose: ");
                            int sendChoice = Integer.parseInt(scanner.nextLine());
                            System.out.println(msg.sentMessage(sendChoice));

                            messagesEnteredSoFar++;
                        }
                        break;
                    case "2":
                        System.out.println("Coming Soon.");
                        break;
                    case "3":
                        quit = true;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option, please choose 1-3.");
                }
            }
        }
    }
}
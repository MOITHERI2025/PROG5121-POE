package prog5121_poe;

import java.util.Scanner;

/**
 * Entry point for QuickChat.
 * Flow: register -> log in -> (only if login succeeds) show the messaging menu.
 * Reference: Written with help from Claude (Anthropic AI assistant), 19 October 2026
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();

        // ---------- Registration ----------
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

        // registerUser checks the username, password and cell number, and returns the messages
        String regMsg = loginSystem.registerUser(username, password, cell, fName, lName);
        System.out.println(regMsg);

        // ---------- Login (only offered if registration succeeded) ----------
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

        // ---------- Messaging (users can only send messages if they logged in) ----------
        if (isLoggedIn) {
            System.out.println("\nWelcome to QuickChat.");

            // The user decides up front how many messages they will enter this session
            System.out.print("How many messages would you like to send this session? ");
            int numMessages = Integer.parseInt(scanner.nextLine());

            boolean quit = false;
            int messagesEnteredSoFar = 0;

            // Main menu loop: keeps running until the user chooses option 3 (Quit)
            while (!quit) {
                System.out.println("\n1) Send Messages");
                System.out.println("2) Show recently sent messages");
                System.out.println("3) Quit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        // Stop the user entering more messages than they asked for
                        if (messagesEnteredSoFar >= numMessages) {
                            System.out.println("You've reached your message limit for this session.");
                            break;
                        }

                        // For loop: lets the user enter exactly the number of messages they chose
                        for (int i = messagesEnteredSoFar; i < numMessages; i++) {
                            System.out.print("Recipient cell number (e.g. +27831234567): ");
                            String recipient = scanner.nextLine();
                            System.out.print("Message text: ");
                            String text = scanner.nextLine();

                            // The loop counter (i) becomes the message number used in the hash
                            Message msg = new Message(recipient, text, i);

                            // Validate the recipient and the message length, then show the hash and ID
                            System.out.println(msg.checkRecipientCell());
                            System.out.println(msg.checkMessageLength());
                            msg.createMessageHash();
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Message ID generated: " + msg.getMessageID());

                            // Let the user send, disregard or store the message
                            System.out.println("1) Send  2) Disregard  3) Store");
                            System.out.print("Choose: ");
                            int sendChoice = Integer.parseInt(scanner.nextLine());
                            System.out.println(msg.sentMessage(sendChoice));

                            messagesEnteredSoFar++;
                        }
                        break;

                    case "2":
                        // Still in development, as required by the brief
                        System.out.println("Coming Soon.");
                        break;

                    case "3":
                        // Ends the while loop, which ends the program
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
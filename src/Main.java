package prog5121_poe;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for QuickChat.
 * Flow: register -> log in -> (only if login succeeds) show the messaging menu.
 * Reference: Written with help from Claude (Anthropic AI assistant), 12 November 2026
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

            // Part 3: the manager holds the arrays; load messages stored in earlier sessions
            MessageManager manager = new MessageManager();
            manager.loadStoredMessages("messages.json");

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
                System.out.println("4) Stored Messages");
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

                            // Part 3: put the message into the matching array
                            String flag = null;
                            switch (sendChoice) {
                                case 1: flag = "Sent"; break;
                                case 2: flag = "Disregard"; break;
                                case 3: flag = "Stored"; break;
                                default: break; // invalid choice: nothing is added
                            }
                            if (flag != null) {
                                manager.addMessage(msg, flag);
                            }

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

                    case "4":
                        // Part 3: the Stored Messages submenu
                        storedMessagesMenu(scanner, manager, fName + " " + lName);
                        break;

                    default:
                        System.out.println("Invalid option, please choose 1-4.");
                }
            }
        }
    }

    // Part 3: the Stored Messages submenu. Loops until the user chooses "g" to go back.
    private static void storedMessagesMenu(Scanner scanner, MessageManager manager, String senderName) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== STORED MESSAGES ===");
            System.out.println("a) Display the sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all messages to a recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display a report of all stored messages");
            System.out.println("g) Back to the main menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println(manager.displaySendersAndRecipients(senderName));
                    break;

                case "b":
                    System.out.println(manager.longestStoredMessage());
                    break;

                case "c":
                    System.out.print("Enter the message ID: ");
                    System.out.println(manager.searchByMessageID(scanner.nextLine().trim()));
                    break;

                case "d": {
                    System.out.print("Enter the recipient's cell number: ");
                    ArrayList<String> results = manager.searchByRecipient(scanner.nextLine().trim());
                    if (results.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : results) {
                            System.out.println(text);
                        }
                    }
                    break;
                }

                case "e":
                    // Hashes are always stored in capitals, so convert what the user types
                    System.out.print("Enter the message hash to delete: ");
                    System.out.println(manager.deleteByHash(scanner.nextLine().trim().toUpperCase()));
                    break;

                case "f":
                    System.out.println(manager.storedReport());
                    break;

                case "g":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option, please choose a-g.");
            }
        }
    }
}
package prog5121_poe;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents one chat message in QuickChat.
 * Handles validation (ID, recipient, length), hash creation,
 * the send/disregard/store choice, and saving messages to JSON.
 */
public class Message {

    // Running total of messages actually sent this session (shared by all messages)
    private static int totalMessagesSent = 0;

    // Every message the user chose to send this session
    private static ArrayList<Message> sentMessages = new ArrayList<>();

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber; // position in this session's loop (0, 1, 2...)

    // Creates a message with a randomly generated 10-digit ID
    public Message(String recipient, String messageText, int messageNumber) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
    }

    // Test-only constructor: lets unit tests supply a fixed ID so the hash is predictable
    public Message(String recipient, String messageText, int messageNumber, String fixedID) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = fixedID;
    }

    // Builds a 10-digit ID by appending ten random digits (0-9)
    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    // Returns true if the message ID is no more than 10 characters long
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Returns true if the number is +27 followed by 9 digits (same rule as Part 1)
    public boolean isRecipientValid() {
        return recipient != null && recipient.matches("^\\+27\\d{9}$");
    }

    // Returns the success or failure message for the recipient's cell number
    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an "
                 + "international code. Please correct the number and try again.";
        }
    }

    // Returns true if the message is 250 characters or fewer
    public boolean isMessageLengthValid() {
        return messageText.length() <= 250;
    }

    // Returns "Message ready to send." or says how many characters over 250 the message is
    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        } else {
            int excess = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // Builds the hash: first 2 ID digits, message number, then first and last words in capitals
    public String createMessageHash() {
        String idPart = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        messageHash = idPart + ":" + messageNumber + ":" + firstWord + lastWord;
        return messageHash;
    }

    // Handles the user's choice: 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                sentMessages.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }

    // Returns the details of every message sent so far this session
    public String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("ID: ").append(m.messageID)
              .append(" | To: ").append(m.recipient)
              .append(" | Msg: ").append(m.messageText)
              .append(" | Hash: ").append(m.messageHash)
              .append("\n");
        }
        return sb.toString();
    }

    // Returns how many messages have been sent so far
    public static int returnTotalMessagess() {
        return totalMessagesSent;
    }

    // Saves the message to messages.json by building the JSON text by hand.
    // Reference: [add the source you used for this approach, e.g. website/author, date accessed]
    public void storeMessage() {
        try {
            File file = new File("messages.json");
            ArrayList<String> existing = new ArrayList<>();

            // If the file already exists, read the earlier messages so we don't overwrite them
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath())).trim();
                if (content.length() > 2) {
                    content = content.substring(1, content.length() - 1);
                    for (String p : content.split("\\},\\{")) {
                        String clean = p.startsWith("{") ? p : "{" + p;
                        clean = clean.endsWith("}") ? clean : clean + "}";
                        existing.add(clean);
                    }
                }
            }

            // Build this message as one JSON object
            String entry = "{"
                    + "\"messageID\":\"" + messageID + "\","
                    + "\"recipient\":\"" + recipient + "\","
                    + "\"message\":\"" + messageText.replace("\"", "\\\"") + "\","
                    + "\"messageHash\":\"" + messageHash + "\"}";
            existing.add(entry);

            // Join all the objects into a JSON array and write it to the file
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < existing.size(); i++) {
                json.append(existing.get(i));
                if (i < existing.size() - 1) json.append(",");
            }
            json.append("]");

            Files.write(file.toPath(), json.toString().getBytes());
        } catch (Exception e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // Getter for the message ID
    public String getMessageID() { return messageID; }

    // Getter for the message hash (call createMessageHash first)
    public String getMessageHash() { return messageHash; }
}
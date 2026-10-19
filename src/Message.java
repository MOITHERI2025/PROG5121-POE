package prog5121_poe;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    private static int totalMessagesSent = 0;
    private static ArrayList<Message> sentMessages = new ArrayList<>();

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber; // position in this session's loop (0, 1, 2...)

    // Normal constructor: random 10-digit ID
    public Message(String recipient, String messageText, int messageNumber) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
    }

    // Overload for unit testing with a fixed/known ID (so hash tests are predictable)
    public Message(String recipient, String messageText, int messageNumber, String fixedID) {
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = messageNumber;
        this.messageID = fixedID;
    }

    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    // ID must not exceed 10 characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Reusing the same +27 regex approach as Part 1's checkCellPhoneNumber
    public boolean isRecipientValid() {
        return recipient != null && recipient.matches("^\\+27\\d{9}$");
    }

    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an "
                 + "international code. Please correct the number and try again.";
        }
    }

    public boolean isMessageLengthValid() {
        return messageText.length() <= 250;
    }

    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        } else {
            int excess = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    public String createMessageHash() {
        String idPart = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        messageHash = idPart + ":" + messageNumber + ":" + firstWord + lastWord;
        return messageHash;
    }

    // 1 = Send, 2 = Disregard, 3 = Store
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

    public static int returnTotalMessagess() {
        return totalMessagesSent;
    }

    // Your own storeMessage() method - manually builds JSON, no external library needed
    public void storeMessage() {
        try {
            File file = new File("messages.json");
            ArrayList<String> existing = new ArrayList<>();

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

            String entry = "{"
                    + "\"messageID\":\"" + messageID + "\","
                    + "\"recipient\":\"" + recipient + "\","
                    + "\"message\":\"" + messageText.replace("\"", "\\\"") + "\","
                    + "\"messageHash\":\"" + messageHash + "\"}";
            existing.add(entry);

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

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
}
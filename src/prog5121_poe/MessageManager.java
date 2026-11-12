package prog5121_poe;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Part 3: holds the arrays of messages and the features that read, search,
 * display and delete them.
 * Reference: Written with help from Claude (Anthropic AI assistant), 4 November 2026
 */
public class MessageManager {

    // The five arrays required by Part 3 (filled while the program runs, nothing is hard-coded)
    private ArrayList<Message> sentMessages = new ArrayList<>();
    private ArrayList<Message> disregardedMessages = new ArrayList<>();
    private ArrayList<Message> storedMessages = new ArrayList<>();
    private ArrayList<String> messageHashes = new ArrayList<>();
    private ArrayList<String> messageIDs = new ArrayList<>();

    // Adds a message to the correct array using its flag: "Sent", "Disregard" or "Stored".
    // The hash and ID are also added to their own arrays.
    public void addMessage(Message message, String flag) {
        if (message.getMessageHash() == null) {
            message.createMessageHash();
        }

        switch (flag.toLowerCase()) {
            case "sent":
                sentMessages.add(message);
                break;
            case "disregard":
                disregardedMessages.add(message);
                break;
            case "stored":
                storedMessages.add(message);
                break;
            default:
                return; // unknown flag: do nothing
        }

        messageHashes.add(message.getMessageHash());
        messageIDs.add(message.getMessageID());
    }

    // Reads messages.json into the stored messages array.
    // Reference: Written with help from Claude (Anthropic AI assistant), 4 November 2026
    public void loadStoredMessages(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return; // nothing stored yet
            }
            String content = new String(Files.readAllBytes(file.toPath()));

            // One match = one JSON object with the four fields we saved
            String regex = "\\{\"messageID\":\"(.*?)\",\"recipient\":\"(.*?)\","
                         + "\"message\":\"((?:\\\\.|[^\"\\\\])*)\",\"messageHash\":\"(.*?)\"\\}";
            Matcher matcher = Pattern.compile(regex).matcher(content);

            int count = 0;
            while (matcher.find()) {
                String text = matcher.group(3).replace("\\\"", "\""); // undo the escaped quotes
                Message m = new Message(matcher.group(2), text, count, matcher.group(1));
                m.setMessageHash(matcher.group(4));
                addMessage(m, "Stored");
                count++;
            }
        } catch (Exception e) {
            System.out.println("Error reading stored messages: " + e.getMessage());
        }
    }

    // a. Sender and recipient of every stored message
    public String displaySendersAndRecipients(String senderName) {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(senderName)
              .append(" | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString();
    }

    // b. The longest stored message (compares text lengths in a loop)
    public String longestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        return longest.getMessageText();
    }

    // c. Search every array for a message ID and return its recipient and message
    public String searchByMessageID(String id) {
        for (Message m : allMessages()) {
            if (m.getMessageID().equals(id)) {
                return "Recipient: " + m.getRecipient() + " | Message: " + m.getMessageText();
            }
        }
        return "No message found with that ID.";
    }

    // d. All sent and stored message texts for one recipient
    public ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.getRecipient().equals(recipient)) {
                results.add(m.getMessageText());
            }
        }
        for (Message m : storedMessages) {
            if (m.getRecipient().equals(recipient)) {
                results.add(m.getMessageText());
            }
        }
        return results;
    }

    // e. Delete a message using its hash, removing it from every array that holds it
    public String deleteByHash(String hash) {
        ArrayList<ArrayList<Message>> lists = new ArrayList<>();
        lists.add(sentMessages);
        lists.add(disregardedMessages);
        lists.add(storedMessages);

        for (ArrayList<Message> list : lists) {
            for (int i = 0; i < list.size(); i++) {
                Message m = list.get(i);
                if (hash.equals(m.getMessageHash())) {
                    list.remove(i);
                    messageHashes.remove(m.getMessageHash());
                    messageIDs.remove(m.getMessageID());
                    return "Message: \"" + m.getMessageText() + "\" successfully deleted.";
                }
            }
        }
        return "No message found with that hash.";
    }

    // f. Report with the full details of every stored message
    public String storedReport() {
        return buildReport(storedMessages);
    }

    // Report with the full details of every sent message (used by the unit test)
    public String sentReport() {
        return buildReport(sentMessages);
    }

    // Builds the report text: hash, ID, recipient and message for each message
    private String buildReport(ArrayList<Message> messages) {
        if (messages.isEmpty()) {
            return "No messages to report.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("Message Hash: ").append(m.getMessageHash()).append("\n")
              .append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getMessageText()).append("\n\n");
        }
        return sb.toString();
    }

    // Combines the three message arrays so a search can look through all of them
    private ArrayList<Message> allMessages() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(disregardedMessages);
        all.addAll(storedMessages);
        return all;
    }

    // Returns the text of every sent message (used by the unit test)
    public ArrayList<String> getSentMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : sentMessages) {
            texts.add(m.getMessageText());
        }
        return texts;
    }

    public int getStoredCount() { return storedMessages.size(); }
    public ArrayList<String> getMessageHashes() { return messageHashes; }
    public ArrayList<String> getMessageIDs() { return messageIDs; }
}
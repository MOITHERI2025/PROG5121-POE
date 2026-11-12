package prog5121_poe;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Part 3 (MessageManager), using the five test messages from the brief.
 * Reference: Written with help from Claude (Anthropic AI assistant), 12 November 2026
 */
public class MessageManagerTest {

    private MessageManager manager;
    private Message msg1, msg2, msg3, msg4, msg5;

    // Runs before every test: builds the five test messages and fills the arrays.
    // Fixed IDs are used so the searches and hashes are predictable.
    @Before
    public void setUp() {
        manager = new MessageManager();

        msg1 = new Message("+27834557896", "Did you get the cake?", 0, "1000000001");
        msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.", 1, "1000000002");
        msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.", 2, "1000000003");
        msg4 = new Message("0838884567", "It is dinner time!", 3, "1000000004");
        msg5 = new Message("+27838884567", "Ok, I am leaving without you.", 4, "1000000005");

        manager.addMessage(msg1, "Sent");
        manager.addMessage(msg2, "Stored");
        manager.addMessage(msg3, "Disregard");
        manager.addMessage(msg4, "Sent");
        manager.addMessage(msg5, "Stored");
    }

    // The sent messages array holds messages 1 and 4
    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time!"),
                manager.getSentMessageTexts());
    }

    // The longest stored message is message 2
    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.longestStoredMessage());
    }

    // Searching for message 4's ID returns its recipient and message
    @Test
    public void testSearchForMessageID() {
        assertEquals("Recipient: 0838884567 | Message: It is dinner time!",
                manager.searchByMessageID("1000000004"));
    }

    // Searching for +27838884567 returns messages 2 and 5
    @Test
    public void testSearchMessagesForRecipient() {
        assertEquals(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                manager.searchByRecipient("+27838884567"));
    }

    // Deleting message 2 by its hash removes it and confirms the deletion
    @Test
    public void testDeleteMessageUsingHash() {
        String result = manager.deleteByHash(msg2.getMessageHash());

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" "
                + "successfully deleted.", result);
        assertEquals(1, manager.getStoredCount());
        assertEquals(Arrays.asList("Ok, I am leaving without you."),
                manager.searchByRecipient("+27838884567"));
    }

    // The sent report lists the hash, ID, recipient and message of each sent message
    @Test
    public void testDisplayReport() {
        String expected = "Message Hash: " + msg1.getMessageHash() + "\n"
                + "Message ID: 1000000001\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message Hash: " + msg4.getMessageHash() + "\n"
                + "Message ID: 1000000004\n"
                + "Recipient: 0838884567\n"
                + "Message: It is dinner time!\n\n";

        assertEquals(expected, manager.sentReport());
    }

    // The hash and ID arrays hold one entry for each of the five messages
    @Test
    public void testHashAndIDArraysPopulated() {
        assertEquals(5, manager.getMessageHashes().size());
        assertEquals(5, manager.getMessageIDs().size());
        assertTrue(manager.getMessageIDs().contains("1000000004"));
    }

    // Searching for an ID that does not exist
    @Test
    public void testSearchForMessageIDNotFound() {
        assertEquals("No message found with that ID.", manager.searchByMessageID("9999999999"));
    }

    // Deleting a hash that does not exist
    @Test
    public void testDeleteUnknownHash() {
        assertEquals("No message found with that hash.", manager.deleteByHash("NOPE"));
    }

    // Reads a small JSON file (written to a temporary file) into the stored array
    @Test
    public void testLoadStoredMessagesFromJson() throws Exception {
        File temp = File.createTempFile("messages", ".json");
        temp.deleteOnExit();

        String json = "[{\"messageID\":\"2000000001\",\"recipient\":\"+27834557896\","
                + "\"message\":\"Did you get the cake?\",\"messageHash\":\"20:0:DIDCAKE\"},"
                + "{\"messageID\":\"2000000002\",\"recipient\":\"+27838884567\","
                + "\"message\":\"Ok, I am leaving without you.\",\"messageHash\":\"20:1:OKYOU\"}]";
        Files.write(temp.toPath(), json.getBytes());

        MessageManager fresh = new MessageManager();
        fresh.loadStoredMessages(temp.getAbsolutePath());

        assertEquals(2, fresh.getStoredCount());
        assertEquals("Ok, I am leaving without you.", fresh.longestStoredMessage());
    }
}
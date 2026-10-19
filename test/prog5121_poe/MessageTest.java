package prog5121_poe;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMessageLengthValid() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    public void testMessageLengthInvalid() {
        String longText = "a".repeat(260);
        Message m = new Message("+27718693002", longText, 0);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", m.checkMessageLength());
    }

    @Test
    public void testRecipientCorrectlyFormatted() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    public void testRecipientIncorrectlyFormatted() {
        Message m = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    @Test
    public void testMessageHashCorrect() {
        // Fixed ID starting with "00" so the hash is predictable
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0, "0012345678");
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void testMessageIDCreated() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertTrue(m.checkMessageID());
        System.out.println("Message ID generated: " + m.getMessageID());
    }

    @Test
    public void testMessageSentChoiceSend() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void testMessageSentChoiceDisregard() {
        Message m = new Message("08575975889", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    public void testMessageSentChoiceStore() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }
}
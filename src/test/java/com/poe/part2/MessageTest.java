package com.poe.part2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MessageTest {

    // Test data from the brief (fixed IDs starting with "00" so hashes are predictable)
    private static final String ID_1 = "0012345678";
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    private static final String ID_2 = "0087654321";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        Message.resetSession();
        // keep tests from touching the real messages.json
        Message.setStorageFile(tempDir.resolve("messages.json"));
    }

    // ---------- message length ----------

    @Test
    public void messageLengthSuccess() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    public void messageLengthFailure() {
        String tooLong = "a".repeat(260);
        Message m = new Message(ID_1, 0, RECIPIENT_1, tooLong);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", m.checkMessageLength());
    }

    // ---------- recipient cell ----------

    @Test
    public void recipientCellSuccess() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    public void recipientCellFailure() {
        Message m = new Message(ID_2, 1, RECIPIENT_2, TEXT_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    // ---------- message hash ----------

    @Test
    public void messageHashTestCase1() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void messageHashesInLoop() {
        String[] ids = {ID_1, ID_2};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {TEXT_1, TEXT_2};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], i, recipients[i], texts[i]);
            assertEquals(expected[i], m.createMessageHash(), "Hash for message " + (i + 1));
            assertEquals(expected[i], m.getMessageHash());
        }
    }

    // ---------- message ID ----------

    @Test
    public void messageIDIsCreated() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message ID generated: " + ID_1, m.messageIDStatus());
        assertTrue(m.checkMessageID());
    }

    @Test
    public void randomMessageIDIsTenDigits() {
        String id = Message.createMessageID();
        assertEquals(10, id.length());
        assertTrue(id.matches("\\d{10}"));
    }

    // ---------- sentMessage (send / disregard / store) ----------

    @Test
    public void sentMessageSend() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void sentMessageDisregard() {
        Message m = new Message(ID_2, 1, RECIPIENT_2, TEXT_2);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    public void sentMessageStore() throws Exception {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        assertEquals("Message successfully stored.", m.sentMessage(3));

        String json = new String(Files.readAllBytes(tempDir.resolve("messages.json")));
        assertTrue(json.contains(ID_1));
        assertTrue(json.contains("00:0:HITONIGHT"));
    }

    // ---------- totals and printing ----------

    @Test
    public void totalSentAfterSendThenDiscard() {
        new Message(ID_1, 0, RECIPIENT_1, TEXT_1).sentMessage(1); // send
        new Message(ID_2, 1, RECIPIENT_2, TEXT_2).sentMessage(2); // discard
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void printMessagesShowsFullDetailsInOrder() {
        Message m = new Message(ID_1, 0, RECIPIENT_1, TEXT_1);
        m.sentMessage(1);
        String expected = "Message ID: " + ID_1
                + "\nMessage Hash: 00:0:HITONIGHT"
                + "\nRecipient: " + RECIPIENT_1
                + "\nMessage: " + TEXT_1;
        assertEquals(expected, Message.printMessages());
    }
}

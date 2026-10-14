package com.poe.part2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

//Represents one QuickChat message and tracks the messages sent while I run the program.
public class Message {

    public static final String CELL_OK = "Cell phone number successfully captured.";
    public static final String CELL_BAD = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    public static final String MESSAGE_READY = "Message ready to send.";
    public static final int MAX_LENGTH = 250;

    // Session state (messages sent while the program is running)
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalSent = 0;
    private static Path storageFile = Paths.get("messages.json");

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // Instance data (Gson stores these fields in the JSON file)
    private final String messageID;
    private final int numMessagesSent;
    private final String recipient;
    private final String message;
    private final String messageHash;

    //Normal constructor: the message ID is randomly generated.
    public Message(int numMessagesSent, String recipient, String message) {
        this(createMessageID(), numMessagesSent, recipient, message);
    }

    //Constructor with a fixed ID and it used by unit tests so the hash is predictable
    public Message(String messageID, int numMessagesSent, String recipient, String message) {
        this.messageID = messageID;
        this.numMessagesSent = numMessagesSent;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    //This generates a random ten-digit number
    public static String createMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public String messageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    /** The message ID must not be more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    /** Same rule as Part 1: international code +27 followed by digits (no more than ten). */
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27\\d{1,10}$")) {
            return CELL_OK;
        }
        return CELL_BAD;
    }

    /** Checks the 250 character limit. */
    public String checkMessageLength() {
        int length = message == null ? 0 : message.length();
        if (length <= MAX_LENGTH) {
            return MESSAGE_READY;
        }
        return "Message exceeds 250 characters by " + (length - MAX_LENGTH) + "; please reduce the size.";
    }

    /** First two digits of ID : message number : FIRSTWORDLASTWORD, in capitals. */
    public String createMessageHash() {
        String prefix = (messageID == null ? "" : messageID.substring(0, Math.min(2, messageID.length())))
                + ":" + numMessagesSent + ":";
        if (message == null || message.trim().isEmpty()) {
            return prefix;
        }
        String[] words = message.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (prefix + first + last).toUpperCase();
    }

    /**
     * Handles the user's choice: 1 = send, 2 = disregard, 3 = store for later.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return storeMessage();
            default:
                return "Invalid option.";
        }
    }

    /** All messages sent while the program is running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.details()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    /** Appends this message to the JSON file (the file holds a JSON array of messages). */
    public String storeMessage() {
        try {
            List<Message> stored = readStoredMessages();
            stored.add(this);
            Files.write(storageFile, GSON.toJson(stored).getBytes(StandardCharsets.UTF_8));
            return "Message successfully stored.";
        } catch (IOException | RuntimeException e) {
            return "Message could not be stored: " + e.getMessage();
        }
    }

    private static List<Message> readStoredMessages() throws IOException {
        if (!Files.exists(storageFile)) {
            return new ArrayList<>();
        }
        String json = new String(Files.readAllBytes(storageFile), StandardCharsets.UTF_8);
        if (json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Message>>() { }.getType();
        List<Message> stored = GSON.fromJson(json, listType);
        return stored == null ? new ArrayList<>() : stored;
    }

    /** Message ID, Message Hash, Recipient, Message (the order the brief asks for). */
    public String details() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    // Helpers for tests
    public static void resetSession() {
        sentMessages.clear();
        totalSent = 0;
    }

    public static void setStorageFile(Path path) {
        storageFile = path;
    }

    public String getMessageID() { return messageID; }
    public int getNumMessagesSent() { return numMessagesSent; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
}

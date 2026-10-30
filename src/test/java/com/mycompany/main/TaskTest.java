package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Task class: message length, ID, hash and JSON handling.
 */
public class TaskTest {

    // Message length: 250 characters is allowed
    @Test
    public void messageOfExactly250CharactersIsAccepted() {
        assertTrue(Task.checkMessageLength("a".repeat(250)));
    }

    // Message length: 251 characters is too long
    @Test
    public void messageOver250CharactersIsRejected() {
        assertFalse(Task.checkMessageLength("a".repeat(251)));
    }

    // Message length: empty or blank messages are not allowed
    @Test
    public void emptyAndBlankMessagesAreRejected() {
        assertFalse(Task.checkMessageLength(""));
        assertFalse(Task.checkMessageLength("   "));
    }

    // A generated ID is always 10 digits and passes the ID check
    @Test
    public void generatedMessageIdIsTenDigits() {
        String id = Task.generateMessageId();
        assertEquals(10, id.length());
        assertTrue(id.matches("[0-9]{10}"));
        assertTrue(new Task(id, 1, "+27831234567", "Hi there").checkMessageID());
    }

    // An ID longer than 10 characters fails the ID check
    @Test
    public void messageIdLongerThanTenFailsCheck() {
        Task task = new Task("12345678901", 1, "+27831234567", "Hi there");
        assertFalse(task.checkMessageID());
    }

    // Hash: first two ID digits : message number : FIRSTWORDLASTWORD
    @Test
    public void messageHashIsCreatedCorrectly() {
        Task task = new Task("0012345678", 1, "+27831234567", "Hi Mike, can you join us for dinner tonight");
        assertEquals("00:1:HITONIGHT", task.createMessageHash());
        assertEquals("00:1:HITONIGHT", task.getMessageHash());
    }

    // Hash: a one word message uses that word for the first and last word
    @Test
    public void messageHashWorksForOneWordMessage() {
        Task task = new Task("9876543210", 3, "+27831234567", "Thanks");
        assertEquals("98:3:THANKSTHANKS", task.getMessageHash());
    }

    // The message details contain the ID, hash, recipient and text
    @Test
    public void printMessageDetailsContainsAllDetails() {
        Task task = new Task("0012345678", 2, "+27831234567", "Hello world");
        String details = task.printMessageDetails();
        assertTrue(details.contains("0012345678"));
        assertTrue(details.contains("00:2:HELLOWORLD"));
        assertTrue(details.contains("+27831234567"));
        assertTrue(details.contains("Hello world"));
    }

    // Messages written to the JSON file are read back with the same values
    @Test
    public void jsonFileCanBeWrittenAndReadBack() throws Exception {
        File file = File.createTempFile("quickchat", ".json");
        file.deleteOnExit();

        ArrayList<Task> original = new ArrayList<>();
        original.add(new Task("1111111111", 1, "+27831111111", "Hello \"quoted\" world"));
        original.add(new Task("2222222222", 2, "+27832222222", "Second message"));

        assertTrue(Task.saveToJsonFile(original, file.getAbsolutePath()));
        ArrayList<Task> loaded = Task.loadFromJsonFile(file.getAbsolutePath());

        assertEquals(2, loaded.size());
        assertEquals("1111111111", loaded.get(0).getMessageId());
        assertEquals("Hello \"quoted\" world", loaded.get(0).getMessageText());
        assertEquals("+27832222222", loaded.get(1).getRecipient());
        assertEquals(original.get(1).getMessageHash(), loaded.get(1).getMessageHash());
    }

    // A missing JSON file gives an empty list instead of crashing
    @Test
    public void loadingMissingJsonFileReturnsEmptyList() {
        assertTrue(Task.loadFromJsonFile("this_file_does_not_exist.json").isEmpty());
    }
}

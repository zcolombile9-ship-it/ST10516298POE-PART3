package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the array features in Main: populating the arrays, longest
 * message, search, delete and the message report.
 */
public class MainTest {

    // Test data that is loaded into the arrays before every test
    private Task messageOne;
    private Task messageTwo;
    private Task messageThree;
    private Task messageFour;

    @BeforeEach
    public void setUp() {
        Main.resetMessageArrays();
        Main.registeredCellphone = "+27830000000";

        messageOne = new Task("1000000001", 1, "+27834557896", "Did you get the cake?");
        messageTwo = new Task("1000000002", 2, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        messageThree = new Task("1000000003", 3, "+27834484567", "Yohoooo, I am at your gate.");
        messageFour = new Task("1000000004", 4, "+27838884567", "It is dinner time!");

        // Send messages one, two and four, disregard three
        Main.addSentMessage(messageOne);
        Main.addSentMessage(messageTwo);
        Main.addSentMessage(messageFour);
        Main.addDisregardedMessage(messageThree);
    }

    // All the parallel arrays are populated and stay the same size
    @Test
    public void sentArraysArePopulatedCorrectly() {
        assertEquals(3, Main.sentMessages.size());
        assertEquals(3, Main.sentRecipients.size());
        assertEquals(3, Main.messageIds.size());
        assertEquals(3, Main.messageHashes.size());
        assertEquals("Did you get the cake?", Main.sentMessages.get(0));
        assertEquals("+27838884567", Main.sentRecipients.get(1));
        assertEquals("1000000004", Main.messageIds.get(2));
        assertEquals("10:4:ITTIME!", Main.messageHashes.get(2));
    }

    // The disregarded array holds the disregarded message
    @Test
    public void disregardedArrayIsPopulated() {
        assertEquals(1, Main.disregardedMessages.size());
        assertEquals("Yohoooo, I am at your gate.", Main.disregardedMessages.get(0));
    }

    // Stored messages are added to the stored array
    @Test
    public void storedArrayIsPopulated() {
        Main.addStoredMessage(messageThree);
        assertEquals(1, Main.storedMessages.size());
        assertEquals("+27834484567", Main.storedMessages.get(0).getRecipient());
    }

    // The total number of sent messages is correct
    @Test
    public void totalMessagesSentIsCorrect() {
        assertEquals(3, Main.returnTotalMessages());
    }

    // The longest message is found and its details are displayed
    @Test
    public void longestMessageIsDisplayed() {
        assertEquals(1, Main.findLongestMessageIndex());
        String output = Main.displayLongestMessage();
        assertTrue(output.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(output.contains("+27838884567"));
    }

    // Longest message with no sent messages does not crash
    @Test
    public void longestMessageWithNoMessagesGivesNotice() {
        Main.resetMessageArrays();
        assertEquals(-1, Main.findLongestMessageIndex());
        assertEquals("No messages have been sent yet.", Main.displayLongestMessage());
    }

    // Searching by message ID returns the recipient and the message
    @Test
    public void searchByMessageIdFindsMessage() {
        String result = Main.searchByMessageId("1000000004");
        assertTrue(result.contains("+27838884567"));
        assertTrue(result.contains("It is dinner time!"));
    }

    // Searching for an ID that does not exist gives a not found notice
    @Test
    public void searchByUnknownMessageIdGivesNotice() {
        assertTrue(Main.searchByMessageId("9999999999").startsWith("No message found"));
    }

    // Searching by recipient returns every message for that number
    @Test
    public void searchByRecipientFindsAllMessages() {
        ArrayList<String> results = Main.searchByRecipient("+27838884567");
        assertEquals(2, results.size());
        assertTrue(results.contains("[Sent] Where are you? You are late! I have asked you to be on time."));
        assertTrue(results.contains("[Sent] It is dinner time!"));
    }

    // Searching by recipient also finds stored messages
    @Test
    public void searchByRecipientIncludesStoredMessages() {
        Main.addStoredMessage(messageThree);
        ArrayList<String> results = Main.searchByRecipient("+27834484567");
        assertEquals(1, results.size());
        assertEquals("[Stored] Yohoooo, I am at your gate.", results.get(0));
    }

    // Searching for a recipient with no messages returns nothing
    @Test
    public void searchByUnknownRecipientReturnsEmptyList() {
        assertTrue(Main.searchByRecipient("+27830000001").isEmpty());
    }

    // Deleting by hash removes the message from every parallel array
    @Test
    public void deleteByHashRemovesMessageFromAllArrays() {
        assertTrue(Main.deleteByMessageHash("10:2:WHERE" + "TIME."));
        assertEquals(2, Main.sentMessages.size());
        assertEquals(2, Main.sentRecipients.size());
        assertEquals(2, Main.messageIds.size());
        assertEquals(2, Main.messageHashes.size());
        assertFalse(Main.sentMessages.contains("Where are you? You are late! I have asked you to be on time."));
        assertFalse(Main.messageIds.contains("1000000002"));
    }

    // Deleting a hash that does not exist changes nothing
    @Test
    public void deleteByUnknownHashChangesNothing() {
        assertFalse(Main.deleteByMessageHash("99:9:NOPE"));
        assertEquals(3, Main.sentMessages.size());
    }

    // The report lists the hash, recipient and message of every sent message
    @Test
    public void reportContainsAllSentMessageDetails() {
        String report = Main.displayMessageReport();
        assertTrue(report.contains("10:1:DIDCAKE?"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("10:4:ITTIME!"));
        assertTrue(report.contains("Total messages sent: 3"));
        // The disregarded message must not appear in the report
        assertFalse(report.contains("Yohoooo"));
    }

    // The report for an empty array does not crash
    @Test
    public void reportWithNoMessagesGivesNotice() {
        Main.resetMessageArrays();
        assertEquals("No messages have been sent yet.", Main.displayMessageReport());
    }

    // The registration checks still work
    @Test
    public void registrationChecksWork() {
        assertTrue(Main.checkUserName("ky_l1"));
        assertFalse(Main.checkUserName("kyle!!!!!!!"));
        assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertFalse(Main.checkPasswordComplexity("password"));
        assertTrue(Main.checkCellphoneNumber("+27838968976"));
        assertFalse(Main.checkCellphoneNumber("08966553"));
    }
}

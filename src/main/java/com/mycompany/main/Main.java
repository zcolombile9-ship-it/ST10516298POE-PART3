/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.main;

/**
 * QuickChat: registers a user, logs them in and lets them send, store or
 * disregard messages. Sent messages are kept in parallel arrays so they can
 * be searched, deleted and reported on.
 *
 * @author Student
 */
//Imports Scanner so we can get user input
import java.util.ArrayList;
import java.util.Scanner;

//Create the Main class
public class Main {

    // Create Scanner for user input
    static Scanner input = new Scanner(System.in);

    // Name of the file the stored messages are saved in
    static final String JSON_FILE = "messages.json";

    // Options the user can choose for every message they write
    static final int SEND = 1;
    static final int DISREGARD = 2;
    static final int STORE = 3;

    //store the user's entered details
    static String username;
    static String password;
    static String cellphone;

    // Stores the registered username
    static String registeredUsername;

    // Store the registered password
    static String registeredPassword;

    // Store the registered cellphone number
    static String registeredCellphone;

    // ---- Sent messages: PARALLEL ARRAYS ----
    // Index 0 of every list below describes the same message
    static ArrayList<String> sentMessages = new ArrayList<>();
    static ArrayList<String> sentRecipients = new ArrayList<>();
    static ArrayList<String> messageIds = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();

    // Messages the user chose to disregard
    static ArrayList<String> disregardedMessages = new ArrayList<>();

    // Messages the user chose to store (saved to and read from the JSON file)
    static ArrayList<Task> storedMessages = new ArrayList<>();

    // Counts every message created, so each message gets its own number
    static int messageCounter = 0;

    // Create the username checking method
    public static boolean checkUserName(String username) {
        //Username must have exactly 5 characters
        //and must contain an underscore
        return username.length() == 5 && username.contains("_");
    }

    // Create the password checking method
    public static boolean checkPasswordComplexity(String password) {
        //password must have at least 8 characters
        //and contains uppercase, lowercase, number and special characters
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    // Create the cellphone checking method
    public static boolean checkCellphoneNumber(String number) {
        // Number must start with +27
        // followed by exactly 9 digits
        return number.matches("^\\+27[0-9]{9}$");
    }

    // Create the registered method, returns true if registration worked
    public static boolean registerUser() {
        // Ask for username
        System.out.print("Enter your username: ");
        username = input.nextLine();
        // Ask for password
        System.out.print("Enter your password: ");
        password = input.nextLine();
        // Ask for cellphone number
        System.out.print("Enter your cellphone number (+27): ");
        cellphone = input.nextLine();

        // Check if all registration details are correct
        if (checkUserName(username)
                && checkPasswordComplexity(password)
                && checkCellphoneNumber(cellphone)) {
            // Save the valid details
            registeredUsername = username;
            registeredPassword = password;
            registeredCellphone = cellphone;
            System.out.println("User registered successfully.");
            return true;
        } else {
            System.out.println("Registration failed.");
            return false;
        }
    }

    // Create the login method
    public static boolean loginUser(String username, String password) {
        // compare entered details with registered details
        return username.equals(registeredUsername)
                && password.equals(registeredPassword);
    }

    // Create the login status method
    public static String returnLoginStatus(boolean loginSuccessful) {
        // Check if login was successful
        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // ------------------------------------------------------------------
    // Array handling (these methods return text so they can be unit tested)
    // ------------------------------------------------------------------

    // Empty every array (used when the program starts and by the unit tests)
    public static void resetMessageArrays() {
        sentMessages.clear();
        sentRecipients.clear();
        messageIds.clear();
        messageHashes.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageCounter = 0;
    }

    // Add a sent message to every parallel array
    public static void addSentMessage(Task message) {
        sentMessages.add(message.getMessageText());
        sentRecipients.add(message.getRecipient());
        messageIds.add(message.getMessageId());
        messageHashes.add(message.getMessageHash());
    }

    // Add a disregarded message to its array
    public static void addDisregardedMessage(Task message) {
        disregardedMessages.add(message.getMessageText());
    }

    // Add a message to the stored array
    public static void addStoredMessage(Task message) {
        storedMessages.add(message);
    }

    // The total number of messages that have been sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // Find the position of the longest sent message, -1 if nothing was sent
    public static int findLongestMessageIndex() {
        int longestIndex = -1;
        for (int i = 0; i < sentMessages.size(); i++) {
            if (longestIndex == -1
                    || sentMessages.get(i).length() > sentMessages.get(longestIndex).length()) {
                longestIndex = i;
            }
        }
        return longestIndex;
    }

    // Describe one sent message using the parallel arrays
    public static String describeSentMessage(int index) {
        return "Message ID: " + messageIds.get(index)
                + "\nMessage Hash: " + messageHashes.get(index)
                + "\nSender: " + registeredCellphone
                + "\nRecipient: " + sentRecipients.get(index)
                + "\nMessage: " + sentMessages.get(index);
    }

    // Display the details of the longest sent message
    public static String displayLongestMessage() {
        int longestIndex = findLongestMessageIndex();
        if (longestIndex == -1) {
            return "No messages have been sent yet.";
        }
        return "Longest message (" + sentMessages.get(longestIndex).length()
                + " characters):\n" + describeSentMessage(longestIndex);
    }

    // Search the sent arrays for a message ID
    public static String searchByMessageId(String id) {
        int index = messageIds.indexOf(id);
        if (index == -1) {
            return "No message found with ID " + id + ".";
        }
        return "Recipient: " + sentRecipients.get(index)
                + "\nMessage: " + sentMessages.get(index);
    }

    // Search the arrays for every message sent (or stored) for a recipient
    public static ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> results = new ArrayList<>();

        // Search the sent parallel arrays
        for (int i = 0; i < sentRecipients.size(); i++) {
            if (sentRecipients.get(i).equals(recipient)) {
                results.add("[Sent] " + sentMessages.get(i));
            }
        }

        // Search the stored messages
        for (Task stored : storedMessages) {
            if (stored.getRecipient().equals(recipient)) {
                results.add("[Stored] " + stored.getMessageText());
            }
        }
        return results;
    }

    // Delete a sent message using its hash. The same position is removed
    // from every parallel array so the arrays stay in step.
    public static boolean deleteByMessageHash(String hash) {
        int index = messageHashes.indexOf(hash.toUpperCase());
        if (index == -1) {
            return false;
        }
        sentMessages.remove(index);
        sentRecipients.remove(index);
        messageIds.remove(index);
        messageHashes.remove(index);
        return true;
    }

    // Build the report of every sent message
    public static String displayMessageReport() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder report = new StringBuilder("===== MESSAGE REPORT =====\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            report.append("\nMessage ").append(i + 1).append(" of ").append(sentMessages.size()).append("\n");
            report.append(describeSentMessage(i)).append("\n");
        }
        report.append("\nTotal messages sent: ").append(returnTotalMessages());
        return report.toString();
    }

    // Show the sender and recipient of every sent message
    public static String displaySentMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder text = new StringBuilder("Recently sent messages:\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            text.append("\nSender: ").append(registeredCellphone)
                    .append("\nRecipient: ").append(sentRecipients.get(i))
                    .append("\nMessage: ").append(sentMessages.get(i)).append("\n");
        }
        return text.toString();
    }

    // Read the JSON file into the stored messages array and return what was read
    public static String readStoredMessagesFromJson() {
        storedMessages.clear();
        storedMessages.addAll(Task.loadFromJsonFile(JSON_FILE));

        if (storedMessages.isEmpty()) {
            return "No stored messages found in " + JSON_FILE + ".";
        }
        StringBuilder text = new StringBuilder("Stored messages read from " + JSON_FILE + ":\n");
        for (Task stored : storedMessages) {
            text.append("\n").append(stored.printMessageDetails()).append("\n");
        }
        return text.toString();
    }

    // ------------------------------------------------------------------
    // User input helpers
    // ------------------------------------------------------------------

    // Read a whole number from the user without crashing on bad input
    public static int readNumber(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            // -1 means the input was not a valid number
            return -1;
        }
    }

    // Ask the user for a valid recipient cellphone number
    public static String askForRecipient() {
        String recipient;
        do {
            System.out.print("Enter the recipient's cellphone number (+27...): ");
            recipient = input.nextLine().trim();
            if (!checkCellphoneNumber(recipient)) {
                System.out.println("Invalid number. It must start with +27 followed by 9 digits.");
            }
        } while (!checkCellphoneNumber(recipient));
        return recipient;
    }

    // Ask the user for a valid message of 250 characters or less
    public static String askForMessage() {
        String messageText;
        do {
            System.out.print("Enter your message (max " + Task.MAX_MESSAGE_LENGTH + " characters): ");
            messageText = input.nextLine();
            if (!Task.checkMessageLength(messageText)) {
                System.out.println("Message must be between 1 and " + Task.MAX_MESSAGE_LENGTH + " characters.");
            }
        } while (!Task.checkMessageLength(messageText));
        return messageText;
    }

    // Ask the user whether to send, disregard or store the message
    public static int askForMessageAction() {
        int choice;
        do {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            choice = readNumber("Enter option (1-3): ");
            if (choice < SEND || choice > STORE) {
                System.out.println("Invalid option. Please enter 1, 2 or 3.");
            }
        } while (choice < SEND || choice > STORE);
        return choice;
    }

    // Let the user create the number of messages they choose
    public static void addMessages() {
        // Ask how many messages will be entered
        int totalMessages = readNumber("How many messages do you want to enter? ");
        if (totalMessages < 1) {
            System.out.println("Please enter a number greater than 0.");
            return;
        }

        // For loop runs once for every message the user wants to enter
        for (int count = 1; count <= totalMessages; count++) {
            System.out.println("\n--- Message " + count + " of " + totalMessages + " ---");
            String recipient = askForRecipient();
            String messageText = askForMessage();

            // The counter gives every message its own number
            messageCounter++;
            Task message = new Task(messageCounter, recipient, messageText);

            // Display the message details
            System.out.println("\nMessage details:");
            System.out.println(message.printMessageDetails());

            // Put the message in the correct array
            switch (askForMessageAction()) {
                case SEND:
                    addSentMessage(message);
                    System.out.println("Message successfully sent.");
                    break;
                case DISREGARD:
                    addDisregardedMessage(message);
                    System.out.println("Message disregarded.");
                    break;
                default:
                    addStoredMessage(message);
                    // Store the messages in the JSON file straight away
                    if (Task.saveToJsonFile(storedMessages, JSON_FILE)) {
                        System.out.println("Message stored in " + JSON_FILE);
                    } else {
                        System.out.println("Could not save the message to " + JSON_FILE);
                    }
            }
        }
        System.out.println("\nTotal messages sent: " + returnTotalMessages());
    }

    // Ask for a message ID and show the matching message
    public static void searchMessageId() {
        System.out.print("Enter the message ID to search for: ");
        System.out.println(searchByMessageId(input.nextLine().trim()));
    }

    // Ask for a recipient and show every message for them
    public static void searchRecipient() {
        System.out.print("Enter the recipient's cellphone number: ");
        String recipient = input.nextLine().trim();
        ArrayList<String> results = searchByRecipient(recipient);

        if (results.isEmpty()) {
            System.out.println("No messages found for " + recipient + ".");
        } else {
            System.out.println("Messages for " + recipient + ":");
            for (String result : results) {
                System.out.println(result);
            }
        }
    }

    // Ask for a message hash and delete the matching message
    public static void deleteMessage() {
        System.out.print("Enter the message hash to delete: ");
        String hash = input.nextLine().trim();
        if (deleteByMessageHash(hash)) {
            System.out.println("Message with hash " + hash.toUpperCase() + " successfully deleted.");
        } else {
            System.out.println("No message found with hash " + hash + ".");
        }
    }

    // Show the welcome message and menu, and keep running until the user quits
    public static void runMenu() {
        System.out.println("\nWelcome to QuickChat.");

        // Load any messages that were stored in an earlier session
        storedMessages.addAll(Task.loadFromJsonFile(JSON_FILE));

        boolean running = true;
        // While loop keeps the application running until the user chooses Quit
        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Display the longest sent message");
            System.out.println("4) Search for a message ID");
            System.out.println("5) Search messages for a recipient");
            System.out.println("6) Delete a message using its hash");
            System.out.println("7) Display message report");
            System.out.println("8) Read stored messages from JSON file");
            System.out.println("9) Quit");

            int choice = readNumber("Enter option (1-9): ");

            switch (choice) {
                case 1:
                    addMessages();
                    break;
                case 2:
                    System.out.println(displaySentMessages());
                    break;
                case 3:
                    System.out.println(displayLongestMessage());
                    break;
                case 4:
                    searchMessageId();
                    break;
                case 5:
                    searchRecipient();
                    break;
                case 6:
                    deleteMessage();
                    break;
                case 7:
                    System.out.println(displayMessageReport());
                    break;
                case 8:
                    System.out.println(readStoredMessagesFromJson());
                    break;
                case 9:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number from 1 to 9.");
            }
        }
    }

    // Main method where the program starts
    public static void main(String[] args) {
        // Call registration method, stop if registration failed
        if (!registerUser()) {
            return;
        }

        // Ask the user to log in with their registered details
        System.out.print("\nLogin - enter your username: ");
        String loginUsername = input.nextLine();
        System.out.print("Login - enter your password: ");
        String loginPassword = input.nextLine();

        // Call login method and show the login status
        boolean loginSuccessful = loginUser(loginUsername, loginPassword);
        System.out.println(returnLoginStatus(loginSuccessful));

        // Only show the menu if the login worked
        if (loginSuccessful) {
            runMenu();
        }
    }
}

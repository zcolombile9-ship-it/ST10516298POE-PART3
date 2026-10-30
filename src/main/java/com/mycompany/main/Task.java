package com.mycompany.main;

//Imports needed for reading and writing the JSON file
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Task represents one QuickChat message.
 * It creates the message ID and message hash, formats the message details
 * and saves / loads messages to and from a JSON file.
 *
 * @author Student
 */
public class Task {

    // The longest message the user may send
    public static final int MAX_MESSAGE_LENGTH = 250;

    // A message ID may not be longer than 10 characters
    public static final int MESSAGE_ID_LENGTH = 10;

    // One shared random generator for creating message IDs
    private static final Random RANDOM = new Random();

    // Details that belong to this message
    private final String messageId;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    // Constructor used when a brand new message is created (ID is generated)
    public Task(int messageNumber, String recipient, String messageText) {
        this(generateMessageId(), messageNumber, recipient, messageText);
    }

    // Constructor used when the ID is already known (e.g. loaded from JSON)
    public Task(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Create a random 10 digit message ID
    public static String generateMessageId() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    // The message must contain text and be 250 characters or less
    public static boolean checkMessageLength(String text) {
        return text != null
                && !text.trim().isEmpty()
                && text.length() <= MAX_MESSAGE_LENGTH;
    }

    // The message ID may not be longer than 10 characters
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= MESSAGE_ID_LENGTH;
    }

    // Hash = first two ID digits : message number : FIRSTWORDLASTWORD
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        return (messageId.substring(0, 2) + ":" + messageNumber + ":"
                + firstWord + lastWord).toUpperCase();
    }

    // Return all of the message details as text
    public String printMessageDetails() {
        return "Message ID: " + messageId
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // ------------------------------------------------------------------
    // JSON handling (written by hand so no external library is needed)
    // ------------------------------------------------------------------

    // Change special characters so the text is safe inside a JSON string
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Convert this message into one JSON object
    public String toJson() {
        return "  {\n"
                + "    \"messageId\": \"" + escapeJson(messageId) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\"\n"
                + "  }";
    }

    // Write every message in the list to the JSON file. True if it worked.
    public static boolean saveToJsonFile(List<Task> messages, String fileName) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            json.append(messages.get(i).toJson());
            // No comma after the last object
            if (i < messages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            Files.write(Paths.get(fileName), json.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Read the JSON file and return the messages in it.
    // Returns an empty list if the file is missing or cannot be read.
    public static ArrayList<Task> loadFromJsonFile(String fileName) {
        ArrayList<Task> messages = new ArrayList<>();
        String json;
        try {
            json = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return messages;
        }

        for (Map<String, String> fields : parseJsonObjects(json)) {
            // Skip any object that is missing a required field
            if (!fields.containsKey("messageId") || !fields.containsKey("recipient")
                    || !fields.containsKey("message") || !fields.containsKey("messageNumber")) {
                continue;
            }
            try {
                messages.add(new Task(fields.get("messageId"),
                        Integer.parseInt(fields.get("messageNumber")),
                        fields.get("recipient"),
                        fields.get("message")));
            } catch (NumberFormatException e) {
                // A bad message number means the object is ignored
            }
        }
        return messages;
    }

    // Split a JSON array of flat objects into a list of key/value maps
    private static List<Map<String, String>> parseJsonObjects(String json) {
        List<Map<String, String>> objects = new ArrayList<>();
        Map<String, String> current = null;
        int i = 0;

        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '{') {
                // A new object starts
                current = new HashMap<>();
                i++;
            } else if (c == '}') {
                // The object ends, store it
                if (current != null) {
                    objects.add(current);
                }
                current = null;
                i++;
            } else if (c == '"' && current != null) {
                // A key, then ':' and then its value
                int[] end = new int[1];
                String key = readJsonString(json, i, end);
                i = end[0];
                while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) {
                    i++;
                }
                if (i < json.length() && json.charAt(i) == '"') {
                    current.put(key, readJsonString(json, i, end));
                    i = end[0];
                } else {
                    // A number value, read until the comma or closing bracket
                    int start = i;
                    while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') {
                        i++;
                    }
                    current.put(key, json.substring(start, i).trim());
                }
            } else {
                i++;
            }
        }
        return objects;
    }

    // Read one quoted JSON string starting at 'start'. end[0] is set to the
    // position just after the closing quote.
    private static String readJsonString(String json, int start, int[] end) {
        StringBuilder text = new StringBuilder();
        int i = start + 1;
        while (i < json.length() && json.charAt(i) != '"') {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                i++;
                char escaped = json.charAt(i);
                switch (escaped) {
                    case 'n':
                        text.append('\n');
                        break;
                    case 'r':
                        text.append('\r');
                        break;
                    case 't':
                        text.append('\t');
                        break;
                    default:
                        text.append(escaped);
                }
            } else {
                text.append(c);
            }
            i++;
        }
        end[0] = i + 1;
        return text.toString();
    }
}

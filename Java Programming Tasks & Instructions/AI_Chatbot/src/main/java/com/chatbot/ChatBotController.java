package com.chatbot;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import opennlp.tools.tokenize.SimpleTokenizer;
import java.util.HashMap;
import java.util.Map;

public class ChatBotController {
    @FXML private TextArea chatArea;
    @FXML private TextField userInput;
    
    private final Map<String, String[]> responses = new HashMap<>();
    private final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    
    public void initialize() {
        // Initialize some basic responses
        responses.put("hello", new String[]{"Hello!", "Hi there!", "Greetings!"});
        responses.put("how are you", new String[]{"I'm doing well, thank you!", "I'm just a bot, but I'm functioning properly!", "All systems go!"});
        responses.put("what's your name", new String[]{"I'm JavaBot, your friendly Java-based chatbot!", "You can call me JavaBot!", "I'm JavaBot, nice to meet you!"});
        responses.put("bye", new String[]{"Goodbye!", "See you later!", "Bye! Have a great day!"});
        
        chatArea.appendText("JavaBot: Hello! I'm JavaBot. How can I help you today?\n\n");
    }
    
    @FXML
    private void handleSendMessage() {
        String message = userInput.getText().trim().toLowerCase();
        if (!message.isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            String response = generateResponse(message);
            chatArea.appendText("JavaBot: " + response + "\n\n");
            userInput.clear();
        }
    }
    
    private String generateResponse(String message) {
        // Simple tokenization and keyword matching
        String[] tokens = tokenizer.tokenize(message);
        
        // Check for exact matches first
        for (String key : responses.keySet()) {
            if (message.contains(key)) {
                String[] possibleResponses = responses.get(key);
                return possibleResponses[(int) (Math.random() * possibleResponses.length)];
            }
        }
        
        // If no exact match, check for partial matches
        for (String token : tokens) {
            if (responses.containsKey(token)) {
                String[] possibleResponses = responses.get(token);
                return possibleResponses[(int) (Math.random() * possibleResponses.length)];
            }
        }
        
        // Default response if no matches found
        return "I'm not sure I understand. Could you rephrase that?";
    }
}

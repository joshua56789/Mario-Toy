package tts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatGeneration {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static List<Message> messages = new ArrayList<>();
    private static String recipe = "";

    public static void clearMessages() {
        messages.clear();
    }

    public static List<Message> getMessages() {
        return messages;
    }

    public static void addMessage(String role, String content) {
        messages.add(new Message(role, content));
    }

    public static void changeRecipe(String newRecipe) {
        recipe = newRecipe;
    }

    public static String buildPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful cooking assistant roleplaying as Mario. Below is the recipe you are guiding me through: \n\n");
        // Extract text file from /prompts
        try (InputStream is = ChatGeneration.class.getResourceAsStream("/prompts/" + recipe + ".txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                prompt.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        prompt.append("\nConversation history so far, respond to the latest user message:\n");
        for (Message msg : messages) {
            prompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }
        System.out.println(prompt.toString());
        return prompt.toString();
    }

    public static String generateResponse() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("OPENAI_API_KEY environment variable not set.");
            return null;
        }
        Gson gson = new Gson();

        // Build single-message conversation using buildPrompt()
        List<Message> convo = new ArrayList<>();
        convo.add(new Message("system", buildPrompt()));

        // Build request JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        requestBody.add("messages", gson.toJsonTree(convo));

        String response = sendPostRequest(API_URL, requestBody.toString());

        if (response == null) {
            System.out.println("Failed to get response from OpenAI.");
            return null;
        }

        // Parse assistant's reply
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String assistantReply = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // Add to messages history
            messages.add(new Message("assistant", assistantReply.trim()));

            return assistantReply.trim();
        } catch (Exception e) {
            System.err.println("Error parsing OpenAI response:");
            e.printStackTrace();
            System.out.println("Raw response: " + response);
            return null;
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Speaking as Mario...");
        TextToSpeech.speak("Hello I am Mario! Let's start cooking!");

        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("OPENAI_API_KEY environment variable not set.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();

        // Store convo history
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful cooking assistant."));

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Exiting chat...");
                break;
            }

            messages.add(new Message("user", userInput));

            // Build request JSON
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "gpt-3.5-turbo");
            requestBody.add("messages", gson.toJsonTree(messages));

            String response = sendPostRequest(API_URL, requestBody.toString());

            if (response == null) {
                System.out.println("Failed to get response from OpenAI.");
                continue;
            }

            // Parse assistant's reply
            try {
                JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                String assistantReply = jsonResponse
                        .getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

                System.out.println("GPT: " + assistantReply.trim());

                messages.add(new Message("assistant", assistantReply.trim()));

            } catch (Exception e) {
                System.err.println("Error parsing OpenAI response:");
                e.printStackTrace();
                System.out.println("Raw response: " + response);
            }
        }

        scanner.close();
    }

    private static String sendPostRequest(String apiUrl, String jsonBody) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = connection.getResponseCode();
            InputStream is = (status < 400) ? connection.getInputStream() : connection.getErrorStream();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return content.toString();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            System.err.println("HTTP Request failed:");
            e.printStackTrace();
            return null;
        }
    }
}
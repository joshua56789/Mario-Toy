package tts;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatGeneration {

    // Replace this with your actual API key
    // get api key from .env file
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("You: ");
        String userInput = scanner.nextLine();

        String requestBody = "{\n" +
            "  \"model\": \"gpt-3.5-turbo\",\n" +
            "  \"messages\": [\n" +
            "    {\"role\": \"system\", \"content\": \"You are a helpful cooking assistant.\"},\n" +
            "    {\"role\": \"user\", \"content\": \"" + userInput + "\"}\n" +
            "  ]\n" +
            "}";

        HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int status = connection.getResponseCode();
        InputStream is = (status < 400) ? connection.getInputStream() : connection.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        String jsonResponse = content.toString();

        try {
            String reply = jsonResponse.split("\"content\":\"")[1].split("\"")[0];
            System.out.println("GPT: " + reply.replace("\\n", "\n"));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(jsonResponse);
        }
    }
}
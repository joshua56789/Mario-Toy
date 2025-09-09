package tts;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.client.okhttp.OpenAIOkHttpClient.*;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletion;

import com.openai.models.chat.completions.ChatCompletionMessage;

import java.util.List;
import java.util.Scanner;

public class ChatGeneration {
    OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    
    public void generate() {
        Scanner scanner = new Scanner(System.in);

        String userMessage = "How do I make spaghetti?";
        String model = "gpt-4o-mini";

        System.out.println("Ask question:");
        userMessage = scanner.nextLine();

        ChatCompletionCreateParams.Builder createParams = ChatCompletionCreateParams.builder()
        .model(model)
        .addSystemMessage("You're helping me follow a recipe while acting like Mario")
        .addUserMessage(userMessage);
        client.chat()
            .completions()
            .create(createParams.build())
            .choices()
            .stream()
            .flatMap(choice -> choice.message()
                .content()
                .stream())
            .forEach(System.out::println);
        do {
            List<ChatCompletionMessage> messages = client.chat()
            .completions()
            .create(createParams.build())
            .choices()
            .stream()
            .map(ChatCompletion.Choice::message)
            .toList();

            messages.stream()
            .flatMap(message -> message.content().stream())
            .forEach(System.out::println);

            System.out.println("-----------------------------------");
            System.out.println("Anything else you would like to know? Otherwise type EXIT to stop the program.");

            String userMessageConversation = scanner.next();

            if ("exit".equalsIgnoreCase(userMessageConversation)) {
            scanner.close();
            return;
            }

            messages.forEach(createParams::addMessage);
            createParams
            .addDeveloperMessage("Continue providing help following the same rules as before.")
            .addUserMessage(userMessageConversation);

        } while (true);
    }
}

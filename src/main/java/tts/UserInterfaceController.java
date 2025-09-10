package tts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

public class UserInterfaceController {
    @FXML
    private Text titleText;
    @FXML
    private Button spaghettiButton;
    @FXML
    private Button pastaButton;
    @FXML
    private Button pizzaButton;
    @FXML
    private Button lasagnaButton;
    @FXML
    private Button garlicBreadButton;
    @FXML
    private Button tiramisuButton;

    @FXML
    private void initialize() {
        System.out.println("Initialized User Interface Controller");
    }

    @FXML
    private static void recipe(String dish) {
        System.out.println("Recipe processing for: " + dish);
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("OPENAI_API_KEY not set.");
            return;
        }

        // Build request JSON
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        String content = new String(Files.readAllBytes(Paths.get("prompts/" + dish + ".txt")));
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", "You are a helpful cooking assistant and you are going to help me cook " + dish + " using this recipe." + content);
        messages.add(systemMsg);

    
    }

    @FXML
    private void spaghettiPressed() {
        System.out.println("Spaghetti button pressed");
        recipe("spaghetti");
    }
    @FXML
    private void pastaPressed() {
        System.out.println("Pasta button pressed");
        recipe("pasta");
    }

    @FXML
    private void pizzaPressed() {
        System.out.println("Pizza button pressed");
        recipe("pizza");
    }

    @FXML
    private void lasagnaPressed() {
        System.out.println("Lasagna button pressed");
        recipe("lasagna");
    }

    @FXML
    private void garlicBreadPressed() {
        System.out.println("Garlic Bread button pressed");
        recipe("garlic bread");
    }

    @FXML
    private void tiramisuPressed() {
        System.out.println("Tiramisu button pressed");
        recipe("tiramisu");
    }

}

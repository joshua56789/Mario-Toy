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
    private static void recipe(String dish) throws IOException {
        System.out.println("Recipe processing for: " + dish);
        String content = new String(Files.readAllBytes(Paths.get("prompts/" + dish + ".txt")));
        ChatGeneration.recipe = content;
        ChatGeneration.clearMessages();
        TextToSpeech.speak("Now we are cooking " + dish + ". Ask me how to start!");
    }

    @FXML
    private void spaghettiPressed() throws IOException {
        System.out.println("Spaghetti button pressed");
        recipe("spaghetti");
    }
    @FXML
    private void pastaPressed() throws IOException {
        System.out.println("Pasta button pressed");
        recipe("pasta");
    }

    @FXML
    private void pizzaPressed() throws IOException {
        System.out.println("Pizza button pressed");
        recipe("pizza");
    }

    @FXML
    private void lasagnaPressed() throws IOException {
        System.out.println("Lasagna button pressed");
        recipe("lasagna");
    }

    @FXML
    private void garlicBreadPressed() throws IOException {
        System.out.println("Garlic Bread button pressed");
        recipe("garlic bread");
    }

    @FXML
    private void tiramisuPressed() throws IOException {
        System.out.println("Tiramisu button pressed");
        recipe("tiramisu");
    }

}

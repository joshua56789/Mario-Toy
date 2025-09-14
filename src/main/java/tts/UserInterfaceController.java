package tts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
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
    private Button playButton;

    private boolean isRecording = false;
    private String userText = "";

    @FXML
    private void initialize() {
        System.out.println("Initialized User Interface Controller");
        new Thread(() -> TextToSpeech.speak("Hello I am Mario, letsa start cooking!")).start();
        new Thread(() -> { 
            MCUInterface mcu = new MCUInterface(() -> this.toggleRecord());
            mcu.monitorMCU();
        }).start();
    }

    @FXML
    private static void recipe(String dish) throws IOException {
        System.out.println("Recipe processing for: " + dish);
        new Thread(() -> TextToSpeech.speak("Now we are cooking " + dish + ". Ask me how to start!")).start();
        ChatGeneration.changeRecipe(dish);
        System.out.println("Recipe loaded: " + ChatGeneration.recipe);
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

   @FXML
    private void toggleRecord() {
        System.out.println("Toggle record button pressed");
        if (isRecording) {
            // Stop recording
            isRecording = false;
            Platform.runLater(() -> playButton.setText("▶"));
            // Do audio processing in a background thread
            new Thread(() -> {
                byte[] audioBytes = SpeechToText.stopRecording();
                String userText = "";
                try {
                    if (audioBytes != null) {
                        userText = SpeechToText.transcribeFromPcmBytes(audioBytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (userText == null || userText.isEmpty()) {
                    userText = "What is the next step?";
                }
                ChatGeneration.addMessage("user", userText);
                System.out.println("User said: " + userText);

                String response = ChatGeneration.generateResponse();
                System.out.println("Mario response: " + response);
                TextToSpeech.speak(response);
                System.out.println("Recording stopped");
            }).start();
        } else {
            isRecording = true;
            Platform.runLater(() -> playButton.setText("⏸"));
            new Thread(SpeechToText::startRecording).start();
            System.out.println("Recording started");
        }
    }

}

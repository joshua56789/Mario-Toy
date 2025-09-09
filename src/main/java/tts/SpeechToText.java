package tts;
// Gradle/Maven dependency: org.vosk:vosk (see Vosk docs for versions)
import org.vosk.Model;
import org.vosk.Recognizer;

public class SpeechToText {
    public static String recognizeWithVosk(byte[] rawPcmBytes) throws Exception {
        // Ensure you have downloaded a Vosk model and point modelPath to it
        try (Model model = new Model("path/to/vosk-model")) {
            // sampleRate must match your data (e.g., 16000)
            try (Recognizer recognizer = new Recognizer(model, 16000.0f)) {
                // feed the raw PCM bytes (16-bit little-endian PCM)
                recognizer.acceptWaveForm(rawPcmBytes, rawPcmBytes.length);
                String resultJson = recognizer.getFinalResult();
                return resultJson; // JSON containing text and confidences
            }
        }
    }
}

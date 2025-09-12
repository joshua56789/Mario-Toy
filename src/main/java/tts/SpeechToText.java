package tts;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.*;
import java.io.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpeechToText {

    /**
     * Transcribe raw PCM bytes (16kHz, 16-bit, mono, signed, little-endian) using Vosk.
     *
     * @param audioBytes raw PCM byte array in the format above
     * @param modelPath  path to the Vosk model directory (e.g. "model")
     * @return the recognized text (empty string if no text)
     * @throws Exception on IO / audio errors
     */
    public static String transcribeFromPcmBytes(byte[] audioBytes) throws Exception {

        try (Model model = new Model("Model");
             // recognizer sample rate must match the audio (16000)
             Recognizer recognizer = new Recognizer(model, 16000.0f)) {

            // Create an AudioInputStream over the raw PCM bytes
            AudioFormat format = new AudioFormat(
                16000.0f, // sample rate
                16,       // sample size in bits
                1,        // channels (mono)
                true,     // signed
                false     // little-endian
            );

            try (ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
                 AudioInputStream ais = new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize())) {

                byte[] buffer = new byte[4096];
                int nbytes;
                while ((nbytes = ais.read(buffer)) > 0) {
                    // Feed raw PCM bytes to Vosk recognizer
                    recognizer.acceptWaveForm(buffer, nbytes);
                }

                String finalJson = recognizer.getFinalResult(); // JSON like {"text":"..."}
                return extractTextFromVoskJson(finalJson);
            }
        }
    }

    // Helper: parse Vosk JSON and return the text field (falls back to raw JSON)
    private static String extractTextFromVoskJson(String json) {
        if (json == null || json.isEmpty()) return "";
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (obj.has("text")) return obj.get("text").getAsString();
        } catch (Exception ignored) {}
        return json;
    }
    public static void main(String[] args)  {
        Path p = Paths.get("hello_pcm_16k_mono.pcm"); 
        byte[] audioBytes;

        String text = "";
        try {
            audioBytes = Files.readAllBytes(p);
            try {
                text = SpeechToText.transcribeFromPcmBytes(audioBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Recognized: " + text);
    }

}

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

    private static TargetDataLine microphone;
    private static ByteArrayOutputStream out;
    private static volatile boolean recording = false;
    private static Thread recordingThread;

    public static void startRecording() {
        AudioFormat format = getAudioFormat();
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            out = new ByteArrayOutputStream();
            recording = true;

            recordingThread = new Thread(() -> {
                byte[] data = new byte[4096];
                while (recording) {
                    int bytesRead = microphone.read(data, 0, data.length);
                    out.write(data, 0, bytesRead);
                }
            });
            recordingThread.start();
            System.out.println("Recording started... (STT)");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static byte[] stopRecording() {
        recording = false;
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        try {
            if (recordingThread != null) {
                recordingThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Recording stopped. (STT)");
        return out != null ? out.toByteArray() : null;
    }

    /**
     * Transcribe raw PCM bytes (16kHz, 16-bit, mono, signed, little-endian) using Vosk.
     *
     * @param audioBytes raw PCM byte array in the format above
     * @param modelPath  path to the Vosk model directory (e.g. "model")
     * @return the recognized text (empty string if no text)
     * @throws Exception on IO / audio errors
     */
    public static String transcribeFromPcmBytes(byte[] audioBytes) throws Exception {

        try (Model model = new Model("src/main/java/Model"); // path to the Vosk model directory
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

    private static AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F; // 16 kHz
        int sampleSizeInBits = 16;
        int channels = 1; // mono
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    // deprecated
    public static byte[] captureAudio(int durationInSeconds) {
        AudioFormat format = getAudioFormat();
        TargetDataLine microphone;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = new byte[4096];

        try {
            // Open microphone line
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            System.out.println("Recording for " + durationInSeconds + " seconds...");

            long end = System.currentTimeMillis() + durationInSeconds * 1000;

            while (System.currentTimeMillis() < end) {
                int bytesRead = microphone.read(data, 0, data.length);
                out.write(data, 0, bytesRead);
            }

            microphone.stop();
            microphone.close();

            System.out.println("Recording stopped.");

            return out.toByteArray();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    // deprecated
    public static String getUserText() throws Exception {
        byte[] audioBytes = captureAudio(5); // Capture 5 seconds of audio
        if (audioBytes != null) {
            return transcribeFromPcmBytes(audioBytes);
        }
        return "";
    }
    
    public static void main(String[] args)  {
        Path p = Paths.get("src/main/java/tts/hello_pcm_16k_mono.pcm"); 
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

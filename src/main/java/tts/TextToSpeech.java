package tts;

public class TextToSpeech {

    public TextToSpeech() {
    }

    public static void speak(String text) {
        try {
            String command = "PowerShell -Command \"Start-Sleep -Milliseconds 200; Add-Type –AssemblyName System.speech; " +
                "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + text.replace("'", "''") + "');\"";

            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
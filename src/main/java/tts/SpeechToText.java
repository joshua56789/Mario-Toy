import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;

import javax.sound.sampled.*;
import java.io.*;

public class SpeechToText {
    public static void speechToText(byte[] audioBytes) throws Exception {
        LibVosk.setLogLevel(0);
        Model model = new Model("model"); // download from alphacephei.com/vosk/models


        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            if (recognizer.acceptWaveForm(audioBytes, audioBytes.length)) {
                System.out.println(recognizer.getResult());
            } else {
                System.out.println(recognizer.getPartialResult());
            }
        }
    }
}

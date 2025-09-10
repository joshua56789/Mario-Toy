package tts;

import javax.sound.sampled.*;
import java.io.*;

public class BytesToWav {
    public static void BytesConverter(byte[] audioBytes) throws Exception {

        // Define the audio format (must match your raw bytes!)
        AudioFormat format = new AudioFormat(
            16000,   // sample rate
            16,      // sample size in bits
            1,       // channels (mono)
            true,    // signed
            false    // little endian
        );

        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream ais = new AudioInputStream(bais, format, audioBytes.length / format.getFrameSize());

        File wavFile = new File("output.wav");
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
    }
}

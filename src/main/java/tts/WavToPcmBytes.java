package tts;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Path;

/**
 * Utility: convert a WAV/file -> raw PCM bytes (16kHz, 16-bit, mono, signed, little-endian).
 * Designed to produce the byte[] expected by SpeechToText.transcribeFromPcmBytes.
 */
public class WavToPcmBytes {

    public static final AudioFormat TARGET_FORMAT = new AudioFormat(
            16000.0f, // sample rate
            16,       // sample size in bits
            1,        // channels (mono)
            true,     // signed
            false     // little-endian
    );

    /**
     * Read an audio file and return raw PCM bytes in the target format.
     *
     * @param wavPath path to the input WAV (or other supported) file
     * @return byte[] raw PCM (no headers)
     * @throws IOException if IO fails
     * @throws UnsupportedAudioFileException if the file isn't a supported audio file
     * @throws IllegalArgumentException if conversion to the target format is not possible
     */
    public static byte[] wavToPcmBytes(Path wavPath)
            throws IOException, UnsupportedAudioFileException, IllegalArgumentException {

        try (AudioInputStream sourceAis = AudioSystem.getAudioInputStream(wavPath.toFile())) {
            AudioInputStream pcmAis = getAudioInputStreamInTargetFormat(sourceAis);

            try (AudioInputStream ais = pcmAis;
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = ais.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            }
        }
    }

    /**
     * Try to obtain an AudioInputStream converted to TARGET_FORMAT.
     * First tries a direct conversion. If that fails, tries intermediate conversion to PCM_SIGNED
     * then to the target. If final conversion isn't supported, throws IllegalArgumentException.
     */
    private static AudioInputStream getAudioInputStreamInTargetFormat(AudioInputStream sourceAis)
            throws UnsupportedAudioFileException {

        AudioFormat sourceFormat = sourceAis.getFormat();

        // If already exactly the target format, return source directly
        if (formatsMatch(sourceFormat, TARGET_FORMAT)) {
            return sourceAis;
        }

        // Direct conversion supported?
        if (AudioSystem.isConversionSupported(TARGET_FORMAT, sourceFormat)) {
            return AudioSystem.getAudioInputStream(TARGET_FORMAT, sourceAis);
        }

        // Try converting to PCM_SIGNED (common intermediate), then to TARGET_FORMAT
        AudioFormat pcmSigned = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2, // frame size for 16-bit
                sourceFormat.getSampleRate(),
                false // little-endian
        );

        if (AudioSystem.isConversionSupported(pcmSigned, sourceFormat)) {
            AudioInputStream intermediate = AudioSystem.getAudioInputStream(pcmSigned, sourceAis);

            if (AudioSystem.isConversionSupported(TARGET_FORMAT, pcmSigned)) {
                return AudioSystem.getAudioInputStream(TARGET_FORMAT, intermediate);
            } else {
                // Can't resample to 16k from this environment
                throw new IllegalArgumentException(
                        "AudioSystem can convert to PCM_SIGNED but cannot resample/convert to the required target format (16kHz mono). " +
                        "Either provide a 16 kHz WAV or install a library that enables sample-rate conversion."
                );
            }
        }

        throw new IllegalArgumentException("Conversion from source audio format to target PCM format is not supported by AudioSystem.");
    }

    private static boolean formatsMatch(AudioFormat a, AudioFormat b) {
        return Float.compare(a.getSampleRate(), b.getSampleRate()) == 0
                && a.getSampleSizeInBits() == b.getSampleSizeInBits()
                && a.getChannels() == b.getChannels()
                && a.isBigEndian() == b.isBigEndian()
                && a.getEncoding().equals(b.getEncoding());
    }

    // Simple CLI example for quick testing
    public static void main(String[] args) throws Exception {
        Path p = Path.of("src/main/java/tts/test.wav"); // adjust path to your WAV file
        byte[] pcm = wavToPcmBytes(p);
        System.out.println("Read " + pcm.length + " PCM bytes (target format: 16kHz, 16-bit, mono).");

        // Example: invoke your existing SpeechToText directly
        String recognized = SpeechToText.transcribeFromPcmBytes(pcm);
        System.out.println("Recognized: " + recognized);
    }
}

package tts;

// Import jSerialComm library for serial communication
import com.fazecast.jSerialComm.SerialPort;


public class MCUInterface {

    @FunctionalInterface
    public interface ButtonToggleListener {
        void onButtonToggle();
    }
    
    ButtonToggleListener toggleListener;

    // MCU port
    SerialPort mcuPort;

    // Uses serial communication to talk to the microcontroller
    // For example, to control LEDs or read button states
    // Uses the jserial library
    public MCUInterface(ButtonToggleListener listener) {
        // Initialize serial communication here
        toggleListener = listener;
        System.out.println("MCU Interface initialized");
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.println("Found port: " + port.getSystemPortName());
            if (port.getSystemPortName().equals("ttyACM0") || port.getSystemPortName().equals("COM3")) {
                port.setBaudRate(115200);
                if (port.openPort()) {
                    System.out.println("Port opened: " + port.getSystemPortName());
                    mcuPort = port;
                } else {
                    System.out.println("Failed to open port: " + port.getSystemPortName());
                }
            }
        }
    }

    // Blocking method that waits for the MCU to send stuff
    public void monitorMCU() {
        
        // Simple loop to read data from MCU
        boolean lastButtonState = false;

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Read any new data from MCU and process it
            if (mcuPort != null && mcuPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[mcuPort.bytesAvailable()];
                int numRead = mcuPort.readBytes(readBuffer, readBuffer.length);
                String received = new String(readBuffer, 0, numRead);
                System.out.println("Received from MCU: " + received);
                // Process the received data
                if (received.contains("BUTTON_PRESS") && !lastButtonState) {
                    System.out.println("Button pressed on MCU");
                    toggleListener.onButtonToggle();
                    lastButtonState = true;
                }
                else if (received.contains("BUTTON_RELEASE") && lastButtonState) {
                    System.out.println("Button released on MCU");
                    toggleListener.onButtonToggle();
                    lastButtonState = false;
                }
            }
        }
    }
    
    public static void sendCommand(String command) {
        // Placeholder for sending command to MCU
        System.out.println("Sending command to MCU: " + command);
    }
}

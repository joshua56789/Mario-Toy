import serial
import time

def try_read_none_message(message: bytearray):
    if len(message) < 1:
        return None
    return "NONE", 1

def try_read_ping_message(message: bytearray):
    if len(message) < 1:
        return None
    return "PING", 1

def try_read_text_message(message: bytearray) -> tuple[str, int]:
    print(f"Trying to read text message from bytes: '{message.hex()}'")
    if len(message) < 3:
        print("Not enough bytes for text message header.")
        return None
    text_length = int.from_bytes(message[1:3], 'little')
    if len(message) < (3 + text_length):
        print(f"Not enough bytes for full text message. Expected length: {text_length}, available: {len(message)-3}")
        return None
    text = message[3:3+text_length].decode('utf-8')
    print(f"Found text message from bytes: '{message[0:3+text_length].hex()}'")
    print(f"Text length: {text_length}, Text: '{text}'")
    return f"TEXT: {text}", 3+text_length

def try_read_audio_message(message: bytearray) -> tuple[str, int]:
    if len(message) < 3:
        return None
    audio_length = int.from_bytes(message[1:3], 'little')
    if len(message) < (3 + audio_length):
        return None
    audio_data = message[3:3+audio_length]
    return f"AUDIO: {len(audio_data)} bytes: " + audio_data.hex(), 3+audio_length

message_start_string = b"MSG_START"
message_read_lookup = {
    0: try_read_none_message,
    1: try_read_ping_message,
    2: try_read_text_message,
    3: try_read_audio_message
}

def get_possible_message(buffer: bytearray):
    start_index = buffer.find(message_start_string)
    if start_index == -1:
        print("No message start found")
        return None
    
    if len(buffer) > len(message_start_string):
        message_type = buffer[len(message_start_string)]
        print(f"Detected message type: {message_type}")
        if message_type in message_read_lookup:
            read_function = message_read_lookup[message_type]
            try:
                read = read_function(buffer[len(message_start_string):])
                if read is None:
                    print("Incomplete message, waiting for more data.")
                    return None
                message, length = read
                del buffer[0:len(message_start_string) + length]

                return message
            except IndexError:
                print("Unknown message format or incomplete message.")
                return None
            

def main():
    pico = serial.Serial('/dev/ttyACM0', 115200, timeout=1)
    pico.reset_input_buffer()

    print("Listening to serial output. Press Ctrl+C to exit.")

    buffer = bytearray()

    try:
        while True:
            if pico.in_waiting > 0:
                inc_bytes = pico.read(500)
                buffer += inc_bytes
                print("Received some bytes: " + inc_bytes.hex())
            message = get_possible_message(buffer)
            if message:
                print(f"Received message: {message}")
            time.sleep(0.1)
    except KeyboardInterrupt:
        print("Program terminated by user.")
    finally:
        pico.close()
        print("Serial connection closed.")


if __name__ == "__main__":
    main()

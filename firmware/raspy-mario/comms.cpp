#include "comms.h"

#include <stdio.h>
#include <string.h>

constexpr uint16_t BUFFER_SIZE = 1024;
const char* message_start_string = "MSG_START";

static uint8_t message_buffer[BUFFER_SIZE]; // Buffer for incoming messages
static uint16_t buffer_index = 0; // Current index in the buffer
static uint16_t unread_bytes = 0; // Number of unread bytes in the buffer
static bool overflow = false; // Buffer overflow flag

// Convenience function to check if a string is within another string
bool string_within(const char* target, const char* within)
{
    return strstr(within, target) != nullptr;
}

void on_usb_cdc_rx_available(void* _param) {
    int c;
    while ((c = getchar_timeout_us(0)) != PICO_ERROR_TIMEOUT) {
        if (unread_bytes < BUFFER_SIZE) {
            message_buffer[(buffer_index + unread_bytes) % BUFFER_SIZE] = static_cast<uint8_t>(c);
            unread_bytes++;
        } else {
            overflow = true; // Buffer overflow
        }
    }
}

uint8_t read_char() {
    // Read from buffer if available, otherwise block until data is available
    while (unread_bytes == 0) {}
    uint8_t c = message_buffer[buffer_index];
    buffer_index = (buffer_index + 1) % BUFFER_SIZE;
    --unread_bytes;
    return c;
}

void read_chars(uint8_t* dest, uint16_t length) {
    // Read 'length' bytes into dest
    for (uint16_t i = 0; i < length; ++i) {
        dest[i] = read_char();
    }
}

bool peak_chars(uint8_t* dest, uint16_t length) {
    // Peek at the next 'length' bytes without removing them from the buffer
    if (unread_bytes < length) {
        return false; // Not enough data
    }
    for (uint16_t i = 0; i < length; ++i) {
        dest[i] = message_buffer[(buffer_index + i) % BUFFER_SIZE];
    }
    return true;
}

void read_all_chars(char* dest) {
    // Read all available characters into dest
    for (uint16_t i = 0; i < unread_bytes; ++i) {
        dest[i] = static_cast<char>(message_buffer[(buffer_index + i) % BUFFER_SIZE]);
    }
    dest[unread_bytes] = '\0'; // Null terminate
    buffer_index = (buffer_index + unread_bytes) % BUFFER_SIZE;
    unread_bytes = 0;
    overflow = false;
}

void setup_comms() {
    // Initialize stdio for serial communication
    stdio_init_all();

    stdio_set_chars_available_callback(on_usb_cdc_rx_available, NULL);
}

void send_message(const Message &msg) {
    // Send the message over serial comms (stdio)
    // This is a blocking call
    printf(message_start_string);

    // Send message type
    fwrite(&msg.type, sizeof(MessageType), 1, stdout);
    if (msg.type == MessageType::MESSAGE) {
        fwrite(&msg.text.length, sizeof(uint16_t), 1, stdout);
        fwrite(msg.text.text, sizeof(char), msg.text.length, stdout);
    } else if (msg.type == MessageType::AUDIO) {
        fwrite(&msg.audio.length, sizeof(uint16_t), 1, stdout);
        fwrite(msg.audio.data, sizeof(uint8_t), msg.audio.length, stdout);
    }
}

void receive_message(Message &msg) {
    // Blocking call to receive a message over serial comms (stdio)
    // This is a blocking call
    // Use only read_char() to read data

    // Read the message type
    msg.type = static_cast<MessageType>(read_char());

    if (msg.type == MessageType::MESSAGE) {
        // Read length
        read_chars(reinterpret_cast<uint8_t*>(&msg.text.length), sizeof(uint16_t));

        // Allocate memory for text
        msg.text.text = new uint8_t[msg.text.length + 1]; // +1 for null terminator
        read_chars(reinterpret_cast<uint8_t*>(msg.text.text), msg.text.length);
        msg.text.text[msg.text.length] = '\0'; // Null terminate
    } else if (msg.type == MessageType::AUDIO) {
        // Read length
        read_chars(reinterpret_cast<uint8_t*>(&msg.audio.length), sizeof(uint16_t));

        // Allocate memory for data
        msg.audio.data = new audio_byte_t[msg.audio.length];
        read_chars(reinterpret_cast<uint8_t*>(msg.audio.data), msg.audio.length);
    }
}


bool poll_message(Message &msg) {
    // Non-blocking call to check if a message is available
    // Returns true if a message was received, false otherwise

    MessageType type;
    if (!peak_chars(reinterpret_cast<uint8_t*>(&type), sizeof(MessageType))) {
        return false; // Not enough data for message type
    }

    if (type == MessageType::MESSAGE || type == MessageType::AUDIO) {
        // Need to peek at length as well
        uint8_t header[3]; // 1 byte for type, 2 bytes for length
        if (!peak_chars(header, sizeof(header))) {
            return false; // Not enough data for full header
        }
        uint16_t length = *(reinterpret_cast<uint16_t*>(&header[1]));
        if (unread_bytes < sizeof(header) + length) {
            return false; // Not enough data for full message
        }
    }

    // Data is available, read the message
    receive_message(msg);
    return true;
}

void send_button_pressed() {
    // Send a special button pressed message
    const char* button_msg = "BUTTON_PRESSED";
    printf(button_msg);
}

void send_button_released() {
    // Send a special button released message
    const char* button_msg = "BUTTON_RELEASED";
    printf(button_msg);
}

bool check_for_new_mood(MarioMood &new_mood) 
{
    // Check if a mood string exists in serial input
    // Ignore the message code because its overkill
    char serial_in[BUFFER_SIZE];
    read_all_chars(serial_in);

    const MarioMood moods[] = {NEUTRAL, HAPPY, ANGRY, RAMPAGE, MURDER, ABEGADOBAGO};

    for (size_t i = 0; i < sizeof(moods) / sizeof(MarioMood); ++i)
    {
        if (string_within(mood_to_string(moods[i]), serial_in))
        {
            new_mood = moods[i];
            return true;
        }
    }
    return false;
}


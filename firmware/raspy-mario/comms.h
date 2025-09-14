#pragma once

/**
 * File containing methods for sending/receiving data over serial comms
 */

#include "pico/stdlib.h"

typedef uint8_t audio_byte_t;

// Message types
// This is the first byte of each message
enum class MessageType : uint8_t {
    // Just in case for some reason we need a no-op
    NONE = 0,

    // Simple ping message to check connection
    PING = 1,

    // Human readable text message
    // Format is:
    // [uint8_t          ][uint16_t        ][char array         ]
    // [MessageType::TEXT][length (2 bytes)][data (length bytes)]
    MESSAGE = 2,

    // Audio data message
    // Format is:
    // [uint8_t           ][uint16_t        ][uint8_t array      ]
    // [MessageType::AUDIO][length (2 bytes)][data (length bytes)]
    AUDIO = 3,
};

struct NoneMessage {
    MessageType type;
};
struct PingMessage {
    MessageType type;
};
struct TextMessage {
    MessageType type;
    uint16_t length;
    uint8_t *text; // Null-terminated string
};
struct AudioMessage {
    MessageType type;
    uint16_t length;
    audio_byte_t *data;
};

union Message {
    MessageType type;
    NoneMessage none;
    PingMessage ping;
    TextMessage text;
    AudioMessage audio;
};

void setup_comms();

// Main interface functions (blocking)
void send_message(const Message &msg);
void receive_message(Message &msg);

// Non-blocking version - returns true if a message was received
bool poll_message(Message &msg);

void send_button_pressed();

void send_button_released();


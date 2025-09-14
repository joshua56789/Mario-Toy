#pragma once

#include "pico/stdlib.h"

constexpr uint16_t MARIO_LEFT_ARM_SERVO_PIN = 13; // GPIO pin for left arm servo
constexpr uint16_t MARIO_RIGHT_ARM_SERVO_PIN = 16; // GPIO pin for right arm servo
constexpr uint16_t SERVO_WRAP_LEVEL = 60000;
constexpr uint16_t SERVO_MIN_PERCENT_DUTY = (uint16_t)(SERVO_WRAP_LEVEL * 0.05f);
constexpr uint16_t SERVO_MAX_PERCENT_DUTY = (uint16_t)(SERVO_WRAP_LEVEL * 0.12f);
constexpr uint16_t SERVO_INC_PER_DEGREE = (SERVO_MAX_PERCENT_DUTY - SERVO_MIN_PERCENT_DUTY) / 180;

// Just for reference, not used in code
constexpr float SERVO_INC_ACCURACY = ((float)SERVO_INC_PER_DEGREE) / ((float)(SERVO_MAX_PERCENT_DUTY - SERVO_MIN_PERCENT_DUTY) / 180.0f);

void setup_servo_on_pin(uint16_t pin); // Initialize a servo on specified pin
void set_servo_angle(uint16_t pin, uint8_t angle); // angle in degrees, 0 to 180

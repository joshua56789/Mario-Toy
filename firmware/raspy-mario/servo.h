#pragma once

#include "pico/stdlib.h"

constexpr uint16_t SERVO_PWM_PIN = 12;
constexpr uint16_t SERVO_WRAP_LEVEL = 60000;
constexpr uint16_t SERVO_MIN_PERCENT_DUTY = (uint16_t)(SERVO_WRAP_LEVEL * 0.05f);
constexpr uint16_t SERVO_MAX_PERCENT_DUTY = (uint16_t)(SERVO_WRAP_LEVEL * 0.12f);
constexpr uint16_t SERVO_INC_PER_DEGREE = (SERVO_MAX_PERCENT_DUTY - SERVO_MIN_PERCENT_DUTY) / 180;
constexpr uint16_t SERVO_ROTATION_SPEED = 20; // WIP - degrees per second

// Just for reference, not used in code
constexpr float SERVO_INC_ACCURACY = ((float)SERVO_INC_PER_DEGREE) / ((float)(SERVO_MAX_PERCENT_DUTY - SERVO_MIN_PERCENT_DUTY) / 180.0f);

void setup_servo();
void set_servo_angle(uint8_t angle); // angle in degrees, 0 to 180

// Servo state for smooth movement (not currently used)
struct ServoState {
    uint8_t current_angle; // Current angle in degrees
    uint8_t target_angle;  // Target angle in degrees
    float increment;       // Increment per update call
    bool moving;           // Is the servo currently moving
};
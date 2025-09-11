#pragma once

#include "pico/stdlib.h"

constexpr uint16_t DAC_PWM_PIN = 20;
constexpr uint16_t DAC_WRAP_LEVEL = 255;
constexpr uint16_t DAC_MIN_PERCENT_DUTY = (uint16_t)(DAC_WRAP_LEVEL * 0.0f);
constexpr uint16_t DAC_MAX_PERCENT_DUTY = (uint16_t)(DAC_WRAP_LEVEL * 1.0f);
constexpr uint16_t DAC_INC_PER_STEP = (DAC_MAX_PERCENT_DUTY - DAC_MIN_PERCENT_DUTY) / DAC_WRAP_LEVEL;
constexpr uint16_t DAC_RESOLUTION = 256; // 8-bit resolution
constexpr uint16_t DAC_MID_PERCENT_DUTY = (DAC_MIN_PERCENT_DUTY + DAC_MAX_PERCENT_DUTY) / 2;
constexpr uint16_t DAC_NOISE_FLOOR = (uint16_t)(DAC_WRAP_LEVEL * 0.02f); // 2% noise floor
constexpr uint16_t DAC_NOISE_CEILING = (uint16_t)(DAC_WRAP_LEVEL * 0.98f); // 98% noise ceiling


void setup_DAC();
void set_DAC_value(uint16_t value); // value from 0 to 65535
uint8_t get_DAC_value(); // returns current DAC value from 0 to 65535
void unmute_DAC();
void update_DAC(); // Call periodically to update DAC state

struct DACState {
    uint8_t current_value; // Current DAC value (0-65535)
    uint8_t target_value;  // Target DAC value (0-65535)
    float value_increment;  // Increment per update call for value
    bool changing_value;    // Is the DAC currently changing value
    bool muted;             // Is the DAC muted
    absolute_time_t last_update_time; // Last time the DAC was updated
};

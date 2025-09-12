#pragma once

#include "pico/stdlib.h"

constexpr uint16_t LED1_PIN = 6;
constexpr uint16_t LED2_PIN = 7;

void setup_leds();

void toggle_led1();
void toggle_led2();
void toggle_both_leds();
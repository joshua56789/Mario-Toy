#pragma once

#include "pico/stdlib.h"

constexpr uint16_t LED1_PIN = 21;
constexpr uint16_t LED2_PIN = 8;

void setup_leds();

void toggle_led1();
void toggle_led2();
void on_led1();
void off_led1();
void on_led2();
void off_led2();
void toggle_both_leds();
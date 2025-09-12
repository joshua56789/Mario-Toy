#include "led.h"

#include "hardware/gpio.h"

void setup_leds() {
    gpio_init(LED1_PIN);
    gpio_set_dir(LED1_PIN, GPIO_OUT);
    gpio_put(LED1_PIN, 0); // Start with LED off

    gpio_init(LED2_PIN);
    gpio_set_dir(LED2_PIN, GPIO_OUT);
    gpio_put(LED2_PIN, 0); // Start with LED off
}

void toggle_led1() {
    gpio_put(LED1_PIN, !gpio_get(LED1_PIN));
}

void toggle_led2() {
    gpio_put(LED2_PIN, !gpio_get(LED2_PIN));
}

void toggle_both_leds() {
    toggle_led1();
    toggle_led2();
}

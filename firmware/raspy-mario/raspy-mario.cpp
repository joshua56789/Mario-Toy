#include <stdio.h>
#include "pico/stdlib.h"
#include "hardware/timer.h"
#include "hardware/clocks.h"
#include "pico/cyw43_arch.h"
#include "hardware/uart.h"
#include "hardware/pwm.h"

#include "servo.h"
#include "DAC.h"
#include "led.h"
#include "tmp_audio.h"
#include "comms.h"

constexpr uint16_t MARIO_LEFT_ARM_SERVO_PIN = 12; // GPIO pin for left arm servo
constexpr uint16_t MARIO_RIGHT_ARM_SERVO_PIN = 7; // GPIO pin for right arm servo

int main()
{
    setup_comms();

    // Initialise the Wi-Fi chip
    if (cyw43_arch_init()) {
        printf("Wi-Fi init failed\n");
        return -1;
    }

    printf("System Clock Frequency is %d Hz\n", clock_get_hz(clk_sys));
    printf("USB Clock Frequency is %d Hz\n", clock_get_hz(clk_usb));
    // For more examples of clocks use see https://github.com/raspberrypi/pico-examples/tree/master/clocks

    // Turn on the pico's LED to show we're running
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, 1);

    // Set GPIO 13 HIGH just for fun
    gpio_init(13);
    gpio_set_dir(13, GPIO_OUT);
    gpio_set_function(13, GPIO_FUNC_SIO);
    gpio_set_drive_strength(13, GPIO_DRIVE_STRENGTH_12MA);

    gpio_put(13, 1);

    setup_servo_on_pin(MARIO_LEFT_ARM_SERVO_PIN);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0); // Start at
    setup_servo_on_pin(MARIO_RIGHT_ARM_SERVO_PIN);
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 0);
    
    setup_DAC();
    set_DAC_value(0); // Start at min value (0-65535)

    // For more examples of UART use see https://github.com/raspberrypi/pico-examples/tree/master/uart

    while (true) {
        printf("Hi\n");
        // Test sending messages
        //Message msg;
        //msg.type = MessageType::PING;
        //send_message(msg);
        //sleep_ms(1000);
        //msg.type = MessageType::MESSAGE;
        //const char* test_str = "Hello from Raspy Mario!";
        //msg.text.length = strlen(test_str);
        //msg.text.text = (uint8_t*)test_str;
        //send_message(msg);
        //sleep_ms(1000);
        //stdio_flush();
        //play_audio();
        //  set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);
        //  sleep_ms(500);
        //  set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 150);
         sleep_ms(500);
    }
}

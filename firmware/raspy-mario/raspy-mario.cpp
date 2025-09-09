#include <stdio.h>
#include "pico/stdlib.h"
#include "hardware/timer.h"
#include "hardware/clocks.h"
#include "pico/cyw43_arch.h"
#include "hardware/uart.h"
#include "hardware/pwm.h"

int64_t alarm_callback(alarm_id_t id, void *user_data) {
    // Put your timeout handler code in here
    return 0;
}



// UART defines
// By default the stdout UART is `uart0`, so we will use the second one
#define UART_ID uart1
#define BAUD_RATE 115200

// Use pins 4 and 5 for UART1
// Pins can be changed, see the GPIO function select table in the datasheet for information on GPIO assignments
#define UART_TX_PIN 4
#define UART_RX_PIN 5



int main()
{
    stdio_init_all();

    // Initialise the Wi-Fi chip
    if (cyw43_arch_init()) {
        printf("Wi-Fi init failed\n");
        return -1;
    }

    // Timer example code - This example fires off the callback after 2000ms
    add_alarm_in_ms(2000, alarm_callback, NULL, false);
    // For more examples of timer use see https://github.com/raspberrypi/pico-examples/tree/master/timer

    printf("System Clock Frequency is %d Hz\n", clock_get_hz(clk_sys));
    printf("USB Clock Frequency is %d Hz\n", clock_get_hz(clk_usb));
    // For more examples of clocks use see https://github.com/raspberrypi/pico-examples/tree/master/clocks

    // Example to turn on the Pico W LED
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, 1);

    // Set up our UART
    uart_init(UART_ID, BAUD_RATE);
    // Set the TX and RX pins by using the function select on the GPIO
    // Set datasheet for more information on function select
    gpio_set_function(UART_TX_PIN, GPIO_FUNC_UART);
    gpio_set_function(UART_RX_PIN, GPIO_FUNC_UART);
    
    // Use some the various UART functions to send out data
    // In a default system, printf will also output via the default UART
    
    // Send out a string, with CR/LF conversions
    uart_puts(UART_ID, " Hello, UART!\n");

    // Setup PWM
    gpio_set_function(12, GPIO_FUNC_PWM);
    gpio_set_function(13, GPIO_FUNC_SIO);
    gpio_set_drive_strength(13, GPIO_DRIVE_STRENGTH_12MA);

    gpio_init(13);

    gpio_set_mask((1 << 12)); // Set GPIO13 HIGH (servo POSITIVE)

    uint pwm_slice_num = pwm_gpio_to_slice_num(12);

    pwm_config config = pwm_get_default_config();

    pwm_config_set_clkdiv_int(&config, 256);
    pwm_config_set_wrap(&config, 100);

    pwm_set_gpio_level(12, 80);

    pwm_init(pwm_slice_num, &config, true);
    
    // For more examples of UART use see https://github.com/raspberrypi/pico-examples/tree/master/uart

    while (true) {
        printf("Hello, world!\n");
        sleep_ms(1000);
    }
}

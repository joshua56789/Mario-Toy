#include "servo.h"

#include "hardware/gpio.h"
#include "hardware/pwm.h"

void setup_servo() {
    gpio_init(SERVO_PWM_PIN);
    gpio_set_function(SERVO_PWM_PIN, GPIO_FUNC_PWM);

    uint pwm_slice_num = pwm_gpio_to_slice_num(SERVO_PWM_PIN);
    pwm_config config = pwm_get_default_config();

    pwm_config_set_clkdiv(&config, 50);
    pwm_config_set_wrap(&config, SERVO_WRAP_LEVEL);

    pwm_init(pwm_slice_num, &config, true);
    pwm_set_gpio_level(SERVO_PWM_PIN, SERVO_MIN_PERCENT_DUTY); // Start at 0 degrees
}

void set_servo_angle(uint8_t angle) {
    if (angle > 180) angle = 180; // Clamp to max 180 degrees
    // Map angle (0-180) to duty cycle (2%-12%)
    uint16_t duty_cycle = SERVO_MIN_PERCENT_DUTY + angle * SERVO_INC_PER_DEGREE;
    uint pwm_slice_num = pwm_gpio_to_slice_num(SERVO_PWM_PIN);
    pwm_set_gpio_level(SERVO_PWM_PIN, duty_cycle);
}

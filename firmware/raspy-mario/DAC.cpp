#include "DAC.h"

#include "hardware/gpio.h"
#include "hardware/pwm.h"


void setup_DAC(){
    gpio_init(DAC_PWM_PIN);
    gpio_set_function(DAC_PWM_PIN, GPIO_FUNC_PWM);

    uint pwm_slice_num = pwm_gpio_to_slice_num(DAC_PWM_PIN);
    pwm_config config = pwm_get_default_config();

    // pwm_config_set_clkdiv(&config, 1);
    pwm_config_set_wrap(&config, DAC_WRAP_LEVEL);

    pwm_init(pwm_slice_num, &config, true);
    pwm_set_gpio_level(DAC_PWM_PIN, DAC_MIN_PERCENT_DUTY); // Start at min value
}

void set_DAC_value(uint16_t value){
    if(value > 4095) value = 4095; // Clamp to max 255
    // Map value (0-65535) to duty cycle (0%-100%)
    uint16_t duty_cycle = DAC_MIN_PERCENT_DUTY + value * DAC_INC_PER_STEP;
    uint pwm_slice_num = pwm_gpio_to_slice_num(DAC_PWM_PIN);
    pwm_set_gpio_level(DAC_PWM_PIN, duty_cycle);
}
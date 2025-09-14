#include "Mario_Mood.h"
#include "led.h"
#include "servo.h"


void mario_happy() {
    off_led1();
    off_led2();
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 150);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);      // Neutral position
}

void mario_angry() {
    on_led1();
    on_led2();
    for(int i = 0; i < 6; i++) {
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 100);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);       // Angry position
        sleep_ms(120);
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 0);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);
        sleep_ms(120);
    }
}

void mario_neutral() {
    off_led1();
    off_led2();
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 0);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);     // Neutral position
}

void mario_rampage() {
        for(int i = 0; i < 6; i++) {
        toggle_both_leds();
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 100);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);       // Angry position
        sleep_ms(100);
        toggle_both_leds();
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 0);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);
        sleep_ms(100);
    }
}

void mario_murder() {
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 100);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);     // Murderous position
    for(int i = 0; i < 20; i++) {
        toggle_both_leds();
        sleep_ms(100);
    }
}

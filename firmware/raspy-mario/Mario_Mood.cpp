#include "Mario_Mood.h"
#include "led.h"
#include "servo.h"


void mario_happy() {
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 150);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);      // Neutral position
}

void mario_angry() {
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
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 0);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);     // Neutral position
}

void mario_murder() {
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 100);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);     // Murderous position
}

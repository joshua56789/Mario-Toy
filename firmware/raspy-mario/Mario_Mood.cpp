#include "Mario_Mood.h"
#include "led.h"
#include "servo.h"


const char* mood_to_string(MarioMood mood) {
    switch (mood) {
        case NEUTRAL:
            return "NEUTRAL";
        case HAPPY:
            return "HAPPY";
        case ANGRY:
            return "ANGRY";
        case RAMPAGE:
            return "RAMPAGE";
        case MURDER:
            return "MURDER";
    }
}

void set_mario_mood(MarioMood mood)
{
    switch (mood)
    {
        case NEUTRAL:
            mario_neutral();
            return;
        case ANGRY:
            mario_angry();
            return;
        case HAPPY:
            mario_happy();
            return;
        case MURDER:
            mario_murder();
            return;
        case RAMPAGE:
            mario_rampage();
            return;
    }
}

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

void mario_abegadobago(){
    off_led1();
    off_led2();
    for(int i = 0; i < 6; i++) {
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 80);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);       // Angry position
        sleep_ms(170);
        set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 20);
        set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 100);
        sleep_ms(170);
    }    // Neutral position
}

void mario_neutral() {
    off_led1();
    off_led2();
    set_servo_angle(MARIO_RIGHT_ARM_SERVO_PIN, 150);
    set_servo_angle(MARIO_LEFT_ARM_SERVO_PIN, 0);     // Neutral position
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

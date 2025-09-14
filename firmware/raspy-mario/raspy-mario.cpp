#include "pico/stdlib.h"
#include "hardware/pwm.h"// Buzzer pin
const uint BUZZER_PIN = 20;// Function to play a tone
void play_tone(uint frequency, float duration) {
    uint slice_num = pwm_gpio_to_slice_num(BUZZER_PIN);

    if (frequency == 0) {
        pwm_set_enabled(slice_num, false); // silence (rest)
    } else {
        // Calculate divider and top for the desired frequency
        float sys_clock = 125000000.0f; // 125 MHz
        float divider = 1.0f;
        uint32_t top = (uint32_t)(sys_clock / (divider * frequency)) - 1;

        // If top is too large, increase divider
        while (top > 65535) {
            divider += 1.0f;
            top = (uint32_t)(sys_clock / (divider * frequency)) - 1;
        }

        pwm_set_clkdiv(slice_num, divider);
        pwm_set_wrap(slice_num, top);
        pwm_set_gpio_level(BUZZER_PIN, top / 2); // 50% duty
        pwm_set_enabled(slice_num, true);
    }
    sleep_ms((int)(duration * 1000));
    pwm_set_enabled(slice_num, false);
}// Frequencies
#define C4  262
#define CS4 277
#define D4  294
#define DS4 311
#define E4  330
#define F4  349
#define FS4 370
#define G4  392
#define GS4 415
#define A4  440
#define AS4 466
#define B4  494
#define C5  523
#define CS5 554
#define D5  587
#define DS5 622
#define E5  659
#define F5  698
#define FS5 740
#define G5  784
#define GS5 831
#define A5  880
#define AS5 932
#define B5  988
#define C6  1047
#define REST 0// Notes and durations
const int notes[] = {
    E5,E5,REST,E5,REST,C5,E5,REST,G5,REST,G4,REST,
    C5,REST,G4,REST,E4,REST,A4,REST,B4,REST,AS4,A4,
    G4,E5,G5,A5,REST,F5,G5,REST,E5,REST,C5,D5,B4,
    C5,REST,G4,REST,E4,REST,A4,REST,B4,REST,AS4,A4,
    G4,E5,G5,A5,REST,F5,G5,REST,E5,REST,C5,D5,B4
};const float durations[] = {
    0.16,0.16,0.08,0.16,0.08,0.16,0.32,0.08,0.32,0.08,0.32,0.16,
    0.32,0.08,0.32,0.08,0.32,0.16,0.16,0.16,0.32,0.08,0.16,0.32,
    0.24,0.24,0.24,0.32,0.12,0.24,0.32,0.12,0.24,0.12,0.24,0.24,0.40,
    0.32,0.08,0.32,0.08,0.32,0.16,0.16,0.16,0.32,0.08,0.16,0.32,
    0.24,0.24,0.24,0.32,0.12,0.24,0.32,0.12,0.24,0.12,0.24,0.24,0.40
};int main() {
    stdio_init_all();    // Configure PWM
    gpio_set_function(BUZZER_PIN, GPIO_FUNC_PWM);    while (true) {
        for (int i = 0; i < sizeof(notes)/sizeof(notes[0]); i++) {
            play_tone(notes[i], durations[i]);
            // sleep_ms(20); // tiny pause
        }
        sleep_ms(1000); // gap before looping
    }
}
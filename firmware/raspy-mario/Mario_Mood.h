#pragma once

#include "pico/stdlib.h"

enum MarioMood {
    NEUTRAL = 0,
    HAPPY,
    ANGRY,
    RAMPAGE,
    MURDER,
    ABEGADOBAGO,
};

const char* mood_to_string(MarioMood mood);

void set_mario_mood(MarioMood mood);
void mario_happy();
void mario_angry();
void mario_abegadobago();
void mario_neutral();
void mario_rampage();
void mario_murder();

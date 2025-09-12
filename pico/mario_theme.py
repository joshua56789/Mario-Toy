from machine import Pin, PWM
import time

buzzer = PWM(Pin(20))

def play_tone(frequency, duration):
    if frequency == 0:  # rest
        buzzer.duty_u16(0)
    else:
        buzzer.freq(frequency)
        buzzer.duty_u16(30000)
    time.sleep(duration)
    buzzer.duty_u16(0)

# Frequencies (Hz)
C4=262;  CS4=277; D4=294; DS4=311; E4=330; F4=349; FS4=370; G4=392; GS4=415; A4=440; AS4=466; B4=494
C5=523;  CS5=554; D5=587; DS5=622; E5=659; F5=698; FS5=740; G5=784; GS5=831; A5=880; AS5=932; B5=988
C6=1047
REST=0

# Main Super Mario Theme (abridged but loops correctly)
# --- Replace ONLY these two arrays ---

# Notes (Super Mario Bros. overworld main loop, corrected)
C4=262;CS4=277;D4=294;DS4=311;E4=330;F4=349;FS4=370;G4=392;GS4=415;A4=440;AS4=466;B4=494
C5=523;CS5=554;D5=587;DS5=622;E5=659;F5=698;FS5=740;G5=784;GS5=831;A5=880;AS5=932;B5=988
C6=1047
REST=0

notes = [
    # Phrase 1
    E5,E5,REST,E5,REST,C5,E5,REST,G5,REST,G4,REST,
    C5,REST,G4,REST,E4,REST,A4,REST,B4,REST,AS4,A4,
    # Phrase 2
    G4,E5,G5,A5,REST,F5,G5,REST,E5,REST,C5,D5,B4,
    # Bridge (corrected rhythm + pitches)
    C5,REST,G4,REST,E4,REST,A4,REST,B4,REST,AS4,A4,
    G4,E5,G5,A5,REST,F5,G5,REST,E5,REST,C5,D5,B4,
]

# Durations (seconds). Eighth ≈ 0.16, sixteenth ≈ 0.08, dotted-quarter ≈ 0.48
durations = [
    # Phrase 1
    0.16,0.16,0.08,0.16,0.08,0.16,0.32,0.08,0.32,0.08,0.32,0.16,
    0.32,0.08,0.32,0.08,0.32,0.16,0.16,0.16,0.32,0.08,0.16,0.32,
    # Phrase 2
    0.24,0.24,0.24,0.32,0.12,0.24,0.32,0.12,0.24,0.12,0.24,0.24,0.40,
    # Bridge (corrected)
    0.32,0.08,0.32,0.08,0.32,0.16,0.16,0.16,0.32,0.08,0.16,0.32,
    0.24,0.24,0.24,0.32,0.12,0.24,0.32,0.12,0.24,0.12,0.24,0.24,0.40,
]


while True:
    for f, d in zip(notes, durations):
        play_tone(f, d)
        time.sleep(0.02)  # tiny pause between notes
    time.sleep(1)  # gap before looping

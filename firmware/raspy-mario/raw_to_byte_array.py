# raw_to_byte_array.py
import sys

def wav_to_byte_array(filename):
    with open(filename, 'rb') as f:
        header = f.read(44)  # Standard WAV header size
        # Parse sample rate, bits per sample, and channels from header
        sample_rate = int.from_bytes(header[24:28], 'little')
        num_channels = int.from_bytes(header[22:24], 'little')
        bits_per_sample = int.from_bytes(header[34:36], 'little')
        bytes_per_sample = bits_per_sample // 8
        # Calculate number of bytes for 5 seconds
        num_samples = sample_rate * 5
        total_bytes = num_samples * num_channels * bytes_per_sample
        data = f.read(total_bytes)
    # Convert bytes to unsigned 8-bit integers (0-255)
    byte_array = [b for b in data]
    return byte_array

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python raw_to_byte_array.py input.wav")
        sys.exit(1)
    arr = wav_to_byte_array(sys.argv[1])
    print(arr)  # or print(bytes(arr)) for a bytes object
#include <iostream>
#include <fstream>
#include <vector>
#include <cstdint>

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " input.raw" << std::endl;
        return 1;
    }

    std::ifstream file(argv[1], std::ios::binary | std::ios::ate);
    if (!file) {
        std::cerr << "Failed to open file" << std::endl;
        return 1;
    }

    std::streamsize size = file.tellg();
    file.seekg(0, std::ios::beg);

    std::vector<int8_t> data(size);
    if (!file.read(reinterpret_cast<char*>(data.data()), size)) {
        std::cerr << "Failed to read file" << std::endl;
        return 1;
    }

    std::cout << "#include <cstdint>\n";
    std::cout << "const uint8_t audio_data[" << size << "] = {\n";
    for (std::streamsize i = 0; i < size; ++i) {
        // Convert signed 8-bit to unsigned 8-bit for PWM (center at 128)
        uint8_t pwm_val = static_cast<uint8_t>(static_cast<int>(data[i]) + 128);
        std::cout << static_cast<int>(pwm_val) << ",";
        if ((i + 1) % 16 == 0) std::cout << "\n";
    }
    std::cout << "\n};\n";

    return 0;
}
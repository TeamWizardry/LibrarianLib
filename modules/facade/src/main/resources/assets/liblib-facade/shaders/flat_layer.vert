#version 150

#include "liblib-albedo:base/transform.glsl"

in vec2 TexelCoord;

out vec2 texelCoord;

void main() {
    gl_Position = albedo_base_transform();
    texelCoord = TexelCoord;
}

#version 150

#include "liblib-albedo:base/transform.glsl"

in vec2 UV;
out vec2 uv;

void main() {
    gl_Position = albedo_base_transform();
    uv = UV;
}

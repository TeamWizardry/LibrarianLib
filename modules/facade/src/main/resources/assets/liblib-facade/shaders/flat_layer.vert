#version 150

#include "liblib-albedo:base/transform.glsl"

in vec2 TexCoord;

out vec2 texCoord;

void main() {
    gl_Position = albedo_base_transform();
    texCoord = TexCoord;
}

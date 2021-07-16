#version 150

#include "liblib-albedo:base/transform.glsl"

in vec4 Color;
out vec4 vertexColor;

void main() {
    gl_Position = albedo_base_transform();

    vertexColor = Color;
}

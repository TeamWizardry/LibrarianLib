#version 150

#include "liblib-albedo:base/transform.glsl"

in vec4 Color;
in vec2 TexCoord;

out vec4 vertexColor;
out vec2 texCoord;

void main() {
    gl_Position = albedo_base_transform();
    vertexColor = Color;
    texCoord = TexCoord;
}

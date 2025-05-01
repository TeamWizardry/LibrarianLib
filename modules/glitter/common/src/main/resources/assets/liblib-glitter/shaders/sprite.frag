#version 150

#include "liblib-albedo:base/fog.glsl"

uniform sampler2D Texture;

in FragmentData
{
    float distance;
    vec4 color;
    vec2 texCoord;
} fs_in;

out vec4 fragColor;

void main() {
    vec4 color = texture(Texture, fs_in.texCoord) * fs_in.color;
    if (color.a < 1./255.) {
        discard;
    }
    fragColor = compute_fog(color, fs_in.distance);
}

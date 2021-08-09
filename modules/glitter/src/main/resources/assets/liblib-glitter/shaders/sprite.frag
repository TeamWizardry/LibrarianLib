#version 150

#include "liblib-albedo:base/fog.glsl"

uniform sampler2D Texture;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(Texture, texCoord) * vertexColor;
    if (color.a < 1./255.) {
        discard;
    }
    fragColor = compute_fog(color, vertexDistance);
}

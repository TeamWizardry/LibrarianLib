#version 150
// flags: ENABLE_TEXTURE, ENABLE_NORMAL, ENABLE_LIGHTMAP, ENABLE_FOG

#if ENABLE_FOG
#include "liblib-albedo:base/fog.glsl"
#endif

in vec4 vertexColor;

#if ENABLE_TEXTURE
uniform sampler2D Texture;
in vec2 texCoord;
#endif

#if ENABLE_FOG
in float distance;
#endif

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;

    #if ENABLE_TEXTURE
    color *= texture(Texture, texCoord);
    #endif

    #if ENABLE_FOG
    color = compute_fog(color, distance);
    #endif

    if (color.a < 1./255.) {
        discard;
    }
    fragColor = color;
}

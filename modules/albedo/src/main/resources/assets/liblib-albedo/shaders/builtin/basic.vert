#version 150
// flags: ENABLE_TEXTURE, ENABLE_NORMAL, ENABLE_LIGHTMAP, ENABLE_FOG

#include "liblib-albedo:include/light.glsl"
#include "liblib-albedo:base/transform.glsl"

// base
in vec4 Color;
out vec4 vertexColor;

#if ENABLE_TEXTURE
in vec2 TexCoord;
out vec2 texCoord;
#endif

#if ENABLE_NORMAL
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
in vec3 Normal;
#endif

#if ENABLE_LIGHTMAP
uniform sampler2D Lightmap;
in ivec2 Light;
#endif

#if ENABLE_FOG
out float distance;
#endif

void main() {
    gl_Position = albedo_base_transform();
    vertexColor = Color;

    #if ENABLE_TEXTURE
    texCoord = TexCoord;
    #endif

    #if ENABLE_NORMAL
    vertexColor *= minecraft_mix_light(Light0_Direction, Light1_Direction, Normal);
    #endif

    #if ENABLE_LIGHTMAP
    vertexColor *= texelFetch(Lightmap, Light / 16, 0);
    #endif

    #if ENABLE_FOG
    distance = length((ModelViewMatrix * vec4(Position, 1.0)).xyz);
    #endif
}


#version 150

#ifdef VERTEX_SHADER
out float vertexDistance;

float albedo_compute_fog() {
    return length((ModelViewMatrix * vec4(Position, 1.0)).xyz);
}
#endif

#ifdef FRAGMENT_SHADER

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;

#include <fog>

vec4 apply_fog(vec4 color) {
    return linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}

#endif
#version 150

#include "liblib-albedo:include/fog.glsl"

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

vec4 compute_fog(vec4 color, float vertexDistance) {
    return linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
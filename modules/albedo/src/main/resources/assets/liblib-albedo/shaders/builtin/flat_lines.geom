#version 150
layout (lines_adjacency) in;
layout (triangle_strip, max_vertices = 4) out;

uniform vec2 DisplaySize;

#include "liblib-albedo:include/lines.glsl"

in GeometryData
{
    vec4 color;
    float insetWidth;
    float outsetWidth;
} gs_in[];

out FragmentData
{
    vec4 color;
} gs_out;

void emit(vec4 pos, vec4 color) {
    gl_Position = pos;
    gs_out.color = color;
    EmitVertex();
}

void main() {
    vec4 screenScale = vec4(DisplaySize, 1, 1);

    vec4 pos0 = gl_in[0].gl_Position;
    vec4 pos1 = gl_in[1].gl_Position;
    vec4 pos2 = gl_in[2].gl_Position;
    vec4 pos3 = gl_in[3].gl_Position;

    vec4 cornerA = pixel_corner(DisplaySize, pos0, pos1, pos2);
    vec4 cornerB = pixel_corner(DisplaySize, pos1, pos2, pos3);
    vec4 outerA = pos1 + cornerA * gs_in[1].outsetWidth;
    vec4 innerA = pos1 + cornerA * -gs_in[1].insetWidth;
    vec4 outerB = pos2 + cornerB * gs_in[2].outsetWidth;
    vec4 innerB = pos2 + cornerB * -gs_in[2].insetWidth;

    emit(outerA, gs_in[1].color);
    emit(outerB, gs_in[2].color);
    emit(innerA, gs_in[1].color);
    emit(innerB, gs_in[2].color);

    EndPrimitive();
}

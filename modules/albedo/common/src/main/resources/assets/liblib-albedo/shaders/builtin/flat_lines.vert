#version 150

#include "liblib-albedo:base/transform.glsl"

in vec4 Color;
in float InsetWidth;
in float OutsetWidth;

out vec4 vertexColor;
out float insetWidth;
out float outsetWidth;

out GeometryData
{
    vec4 color;
    float insetWidth;
    float outsetWidth;
} vs_out;

void main() {
    gl_Position = albedo_base_transform();
    vs_out.color = Color;
    vs_out.insetWidth = InsetWidth;
    vs_out.outsetWidth = OutsetWidth;
}

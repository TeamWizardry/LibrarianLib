#version 330
layout(points) in;
layout(triangle_strip, max_vertices=4) out;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;
uniform mat4 WorldMatrix;

in GeometryData
{
    vec3 position;
    mat2x3 matrix;
    float distance;
    vec4 color;
    vec4 texCoords;
} gs_in[];

out FragmentData
{
    float distance;
    vec4 color;
    vec2 texCoord;
} gs_out;

void emit(vec2 offset, vec2 texCoord) {
    gl_Position = ProjectionMatrix * ModelViewMatrix * WorldMatrix * vec4(gs_in[0].position + gs_in[0].matrix * offset, 1.0);
    gs_out.distance = gs_in[0].distance;
    gs_out.color = gs_in[0].color;
    gs_out.texCoord = texCoord;
    EmitVertex();
}

void main()
{
    vec4 tex = gs_in[0].texCoords;
    emit(vec2(-1, +1), tex.xy); // top-left
    emit(vec2(-1, -1), tex.xw); // bottom-left
    emit(vec2(+1, +1), tex.zy); // top-right
    emit(vec2(+1, -1), tex.zw); // bottom-right
    EndPrimitive();
}
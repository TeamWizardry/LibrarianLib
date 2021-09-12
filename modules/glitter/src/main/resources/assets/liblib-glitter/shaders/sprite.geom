#version 150
layout(points) in;
layout(triangle_strip, max_vertices=8) out;

#include "liblib-albedo:include/light.glsl"

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;
uniform mat4 WorldMatrix;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform bool EnableDiffuseLighting;
uniform bool EnableDiffuseBackface;
uniform mat3 NormalMatrix;

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

void emit(vec2 offset, vec2 texCoord, float light) {
    gl_Position = ProjectionMatrix * ModelViewMatrix * WorldMatrix * vec4(gs_in[0].position + gs_in[0].matrix * offset, 1.0);
    gs_out.distance = gs_in[0].distance;
    vec4 color = gs_in[0].color;
    gs_out.color = vec4(color.rgb * light, color.a);
    gs_out.texCoord = texCoord;
    EmitVertex();
}

void main() {
    vec3 normal = normalize(cross(gs_in[0].matrix[0], gs_in[0].matrix[1]));
    float light = EnableDiffuseLighting ? minecraft_mix_light(Light0_Direction, Light1_Direction, NormalMatrix, normal) : 1.;

    vec4 tex = gs_in[0].texCoords;
    emit(vec2(-1, +1), tex.xy, light); // top-left
    emit(vec2(-1, -1), tex.xw, light); // bottom-left
    emit(vec2(+1, +1), tex.zy, light); // top-right
    emit(vec2(+1, -1), tex.zw, light); // bottom-right
    EndPrimitive();

    if(EnableDiffuseBackface) {
        normal = -normal;
        light = minecraft_mix_light(Light0_Direction, Light1_Direction, NormalMatrix, normal);

        vec4 tex = gs_in[0].texCoords;
        emit(vec2(-1, -1), tex.xw, light); // bottom-left
        emit(vec2(-1, +1), tex.xy, light); // top-left
        emit(vec2(+1, -1), tex.zw, light); // bottom-right
        emit(vec2(+1, +1), tex.zy, light); // top-right
        EndPrimitive();
    }

}
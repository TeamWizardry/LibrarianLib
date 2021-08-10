#version 150

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

uniform mat4 WorldMatrix;
uniform bool UpDominant;

//uniform mat3 NormalMatrix;

//uniform sampler2D Lightmap;

in vec3 Position;
in vec3 Up;
in vec3 Facing;
in vec2 Size;

in vec4 Color;
in vec4 TexCoords;
//in ivec2 Lightmap;

out GeometryData
{
    vec3 position;
    mat2x3 matrix;
    float distance;
    vec4 color;
    vec4 texCoords;
} vs_out;

void main() {
    vec3 right = normalize(cross(Up, Facing));
    vec3 up = normalize(UpDominant ? Up : cross(Facing, right));

    vs_out.position = Position;
    vs_out.matrix = mat2x3(right * Size.x, up * Size.y);
    vs_out.distance = length((ModelViewMatrix * WorldMatrix * vec4(Position, 1.0)).xyz);
    vs_out.color = Color;
    vs_out.texCoords = TexCoords;
}

#version 150

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

//uniform mat3 NormalMatrix;

//uniform sampler2D Lightmap;

in vec3 Point;
in vec3 Up;
in vec3 Right;
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
    vs_out.position = Point;
    vs_out.matrix = mat2x3(Right * Size.x, Up * Size.y);
    vs_out.distance = length((ModelViewMatrix * vec4(Point, 1.0)).xyz);
    vs_out.color = Color;
    vs_out.texCoords = TexCoords;
}

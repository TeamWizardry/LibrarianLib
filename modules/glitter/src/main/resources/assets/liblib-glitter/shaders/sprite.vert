#version 150

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

uniform mat4 WorldMatrix;
//uniform mat3 NormalMatrix;

//uniform sampler2D Lightmap;

in vec3 Point;
in vec3 Up;
in vec3 Right;
in vec2 Offset;

in vec4 Color;
in vec2 TexCoord;
//in ivec2 Lightmap;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord;

void main() {
    vec3 position = Point + Up * Offset.y + Right * Offset.x;
    gl_Position = ProjectionMatrix * ModelViewMatrix * vec4(position, 1.0);

    vertexDistance = length((ModelViewMatrix * vec4(Point, 1.0)).xyz);
    vertexColor = Color;
    texCoord = TexCoord;
}

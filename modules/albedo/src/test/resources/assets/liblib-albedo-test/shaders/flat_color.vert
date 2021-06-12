#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

out vec4 vertexColor;

void main() {
    gl_Position = ProjectionMatrix * ModelViewMatrix * vec4(Position, 1.0);
//    gl_Position = vec4(Position, 1.0);

    vertexColor = Color;
}

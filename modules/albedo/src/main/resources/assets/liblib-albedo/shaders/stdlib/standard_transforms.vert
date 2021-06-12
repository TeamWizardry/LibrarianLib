in vec3 Position;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

#define STANDARD_TRANSFORMS gl_Position = ProjectionMatrix * ModelViewMatrix * vec4(Position, 1.0)

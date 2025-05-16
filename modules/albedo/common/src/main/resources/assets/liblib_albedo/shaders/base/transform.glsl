#version 150

in vec3 Position;
uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

// put this at the top of your main()
vec4 albedo_base_transform() {
    return ProjectionMatrix * ModelViewMatrix * vec4(Position, 1.0);
}
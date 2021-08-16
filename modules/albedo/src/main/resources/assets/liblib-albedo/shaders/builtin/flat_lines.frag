#version 150

out vec4 fragColor;

in FragmentData
{
    vec4 color;
} fs_in;

void main() {
    vec4 color = fs_in.color;
    if (color.a < 1./255.) {
        discard;
    }
    fragColor = color;
}

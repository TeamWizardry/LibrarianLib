#version 150

uniform float Hue;

in vec2 texCoord;
out vec4 fragColor;

// https://stackoverflow.com/a/17897228/1541907
// All components are in the range [0…1], including hue.
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 hsv = vec3(Hue, texCoord.xy);
    vec3 rgb = hsv2rgb(hsv);
    fragColor = vec4(rgb, 1.0);
}

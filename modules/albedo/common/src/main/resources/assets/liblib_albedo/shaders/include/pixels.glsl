#version 150

uniform vec2 DisplaySize;

vec2 to_pixels(vec2 pos) {
    return pos.xy * DisplaySize / 2.;
}

vec2 from_pixels(vec2 pos) {
    return pos.xy / (DisplaySize / 2.);
}

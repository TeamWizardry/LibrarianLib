#version 150

#include "liblib_albedo:base/transform.glsl"

void main() {
    gl_Position = albedo_base_transform();
}

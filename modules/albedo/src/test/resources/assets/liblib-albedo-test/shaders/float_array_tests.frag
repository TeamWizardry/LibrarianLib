#version 330

uniform int index;
uniform float[2] primitive;
float[2] primitive_expected = float[2](10, 20);
uniform vec2[2] vector2;
vec2[2] vector2_expected = vec2[2](vec2(10, 20), vec2(30, 40));
uniform vec3[2] vector3;
vec3[2] vector3_expected = vec3[2](vec3(10, 20, 30), vec3(40, 50, 60));
uniform vec4[2] vector4;
vec4[2] vector4_expected = vec4[2](vec4(10, 20, 30, 40), vec4(50, 60, 70, 80));

#include "gridutil.glsl"

ivec2 dimensions = ivec2(4, 4);
void main() {
    if(isBorder(dimensions, 32)) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 1, 1)) {
        TEST_BOOL(primitive[index] == primitive_expected[index]);
    } else if(AREA(0, 1, 2, 1)) {
        TEST_VECTOR(vector2[index], vector2_expected[index]);
    } else if(AREA(0, 2, 3, 1)) {
        TEST_VECTOR(vector3[index], vector3_expected[index]);
    } else if(AREA(0, 3, 4, 1)) {
        TEST_VECTOR(vector4[index], vector4_expected[index]);
    }

    TEST_NA;
}

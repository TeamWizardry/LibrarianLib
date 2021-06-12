#version 330

uniform int index;
uniform bool[2] primitive;
bool[2] primitive_expected = bool[2](true, false);
uniform bvec2[2] vector2;
bvec2[2] vector2_expected = bvec2[2](bvec2(true, false), bvec2(false, true));
uniform bvec3[2] vector3;
bvec3[2] vector3_expected = bvec3[2](bvec3(true, false, true), bvec3(false, true, false));
uniform bvec4[2] vector4;
bvec4[2] vector4_expected = bvec4[2](bvec4(true, false, true, false), bvec4(false, true, false, true));

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

#version 120

uniform bool primitive;
bool primitive_expected = true;
uniform bvec2 vector2;
bvec2 vector2_expected = bvec2(true, false);
uniform bvec3 vector3;
bvec3 vector3_expected = bvec3(true, false, true);
uniform bvec4 vector4;
bvec4 vector4_expected = bvec4(true, false, true, false);

#pragma include <gridutil.glsl>

ivec2 dimensions = ivec2(4, 4);
void main() {
    if(isBorder(dimensions, 32)) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 1, 1)) {
        TEST_BOOL(primitive == primitive_expected);
    } else if(AREA(0, 1, 2, 1)) {
        TEST_VECTOR(vector2, vector2_expected);
    } else if(AREA(0, 2, 3, 1)) {
        TEST_VECTOR(vector3, vector3_expected);
    } else if(AREA(0, 3, 4, 1)) {
        TEST_VECTOR(vector4, vector4_expected);
    }

    TEST_NA;
}

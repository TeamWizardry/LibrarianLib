#version 150

uniform int index;
// 4xN
uniform mat4x4[2] matrix4x4;
mat4x4[2] matrix4x4_expected = mat4x4[2](
mat4x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31, // column 1
    02, 12, 22, 32, // column 2
    03, 13, 23, 33  // column 3
),
mat4x4(
    100, 110, 120, 130, // column 0
    101, 111, 121, 131, // column 1
    102, 112, 122, 132, // column 2
    103, 113, 123, 133  // column 3
)
);

uniform mat4x3[2] matrix4x3;
mat4x3[2] matrix4x3_expected = mat4x3[](
mat4x3(
    00, 10, 20, // column 0
    01, 11, 21, // column 1
    02, 12, 22, // column 2
    03, 13, 23  // column 3
),
mat4x3(
    100, 110, 120, // column 0
    101, 111, 121, // column 1
    102, 112, 122, // column 2
    103, 113, 123  // column 3
)
);

uniform mat4x2[2] matrix4x2;
mat4x2[2] matrix4x2_expected = mat4x2[](
mat4x2(
    00, 10, // column 0
    01, 11, // column 1
    02, 12, // column 2
    03, 13  // column 3
),
mat4x2(
    100, 110, // column 0
    101, 111, // column 1
    102, 112, // column 2
    103, 113  // column 3
)
);

// 3xN
uniform mat3x4[2] matrix3x4;
mat3x4[2] matrix3x4_expected = mat3x4[](
mat3x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31, // column 1
    02, 12, 22, 32  // column 2
),
mat3x4(
    100, 110, 120, 130, // column 0
    101, 111, 121, 131, // column 1
    102, 112, 122, 132  // column 2
)
);

uniform mat3x3[2] matrix3x3;
mat3x3[2] matrix3x3_expected = mat3x3[](
mat3x3(
    00, 10, 20, // column 0
    01, 11, 21, // column 1
    02, 12, 22  // column 2
),
mat3x3(
    100, 110, 120, // column 0
    101, 111, 121, // column 1
    102, 112, 122  // column 2
)
);

uniform mat3x2[2] matrix3x2;
mat3x2[2] matrix3x2_expected = mat3x2[](
mat3x2(
    00, 10, // column 0
    01, 11, // column 1
    02, 12  // column 2
),
mat3x2(
    100, 110, // column 0
    101, 111, // column 1
    102, 112  // column 2
)
);

// 2xN
uniform mat2x4[2] matrix2x4;
mat2x4[2] matrix2x4_expected = mat2x4[](
mat2x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31  // column 1
),
mat2x4(
    100, 110, 120, 130, // column 0
    101, 111, 121, 131  // column 1
)
);

uniform mat2x3[2] matrix2x3;
mat2x3[2] matrix2x3_expected = mat2x3[](
mat2x3(
    00, 10, 20, // column 0
    01, 11, 21  // column 1
),
mat2x3(
    100, 110, 120, // column 0
    101, 111, 121  // column 1
)
);

uniform mat2x2[2] matrix2x2;
mat2x2[2] matrix2x2_expected = mat2x2[](
mat2x2(
    00, 10, // column 0
    01, 11  // column 1
),
mat2x2(
    100, 110, // column 0
    101, 111  // column 1
)
);

#include "gridutil.glsl"


ivec2 dimensions = ivec2(9, 9);
void main() {
    if(isBorder(dimensions, 16) || any(isInThickLine(dimensions, 16, 4)) || any(isInThickLine(dimensions, 16, 7))) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 4, 4)) {
        TEST_MATRIX(matrix4x4[index], matrix4x4_expected[index]);
    } else if(AREA(0, 4, 4, 3)) {
        TEST_MATRIX(matrix4x3[index], matrix4x3_expected[index]);
    } else if(AREA(0, 7, 4, 2)) {
        TEST_MATRIX(matrix4x2[index], matrix4x2_expected[index]);
    }

    if(AREA(4, 0, 3, 4)) {
        TEST_MATRIX(matrix3x4[index], matrix3x4_expected[index]);
    } else if(AREA(4, 4, 3, 3)) {
        TEST_MATRIX(matrix3x3[index], matrix3x3_expected[index]);
    } else if(AREA(4, 7, 3, 2)) {
        TEST_MATRIX(matrix3x2[index], matrix3x2_expected[index]);
    }

    if(AREA(7, 0, 2, 4)) {
        TEST_MATRIX(matrix2x4[index], matrix2x4_expected[index]);
    } else if(AREA(7, 4, 2, 3)) {
        TEST_MATRIX(matrix2x3[index], matrix2x3_expected[index]);
    } else if(AREA(7, 7, 2, 2)) {
        TEST_MATRIX(matrix2x2[index], matrix2x2_expected[index]);
    }

    TEST_NA;
}

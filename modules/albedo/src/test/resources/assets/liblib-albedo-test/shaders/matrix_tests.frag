#version 330

// 4xN
uniform mat4x4 matrix4x4;
mat4x4 matrix4x4_expected = mat4x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31, // column 1
    02, 12, 22, 32, // column 2
    03, 13, 23, 33  // column 3
);

uniform mat4x3 matrix4x3;
mat4x3 matrix4x3_expected = mat4x3(
    00, 10, 20, // column 0
    01, 11, 21, // column 1
    02, 12, 22, // column 2
    03, 13, 23  // column 3
);

uniform mat4x2 matrix4x2;
mat4x2 matrix4x2_expected = mat4x2(
    00, 10, // column 0
    01, 11, // column 1
    02, 12, // column 2
    03, 13  // column 3
);

// 3xN
uniform mat3x4 matrix3x4;
mat3x4 matrix3x4_expected = mat3x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31, // column 1
    02, 12, 22, 32  // column 2
);

uniform mat3x3 matrix3x3;
mat3x3 matrix3x3_expected = mat3x3(
    00, 10, 20, // column 0
    01, 11, 21, // column 1
    02, 12, 22  // column 2
);

uniform mat3x2 matrix3x2;
mat3x2 matrix3x2_expected = mat3x2(
    00, 10, // column 0
    01, 11, // column 1
    02, 12  // column 2
);

// 2xN
uniform mat2x4 matrix2x4;
mat2x4 matrix2x4_expected = mat2x4(
    00, 10, 20, 30, // column 0
    01, 11, 21, 31  // column 1
);

uniform mat2x3 matrix2x3;
mat2x3 matrix2x3_expected = mat2x3(
    00, 10, 20, // column 0
    01, 11, 21  // column 1
);

uniform mat2x2 matrix2x2;
mat2x2 matrix2x2_expected = mat2x2(
    00, 10, // column 0
    01, 11  // column 1
);

#pragma include <gridutil.glsl>


ivec2 dimensions = ivec2(9, 9);
void main() {
    if(isBorder(dimensions, 16) || any(isInThickLine(dimensions, 16, 4)) || any(isInThickLine(dimensions, 16, 7))) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 4, 4)) {
//        TEST_BOOL(rel == ivec2(1, 1));
//        TEST_BOOL(matrix4x2[1][1] == matrix4x2_expected[1][1]);
//        TEST_BOOL(matrix4x4[1][1] == 0);
        TEST_MATRIX(matrix4x4, matrix4x4_expected);
    } else if(AREA(0, 4, 4, 3)) {
        TEST_MATRIX(matrix4x3, matrix4x3_expected);
    } else if(AREA(0, 7, 4, 2)) {
        TEST_MATRIX(matrix4x2, matrix4x2_expected);
    }

    if(AREA(4, 0, 3, 4)) {
        TEST_MATRIX(matrix3x4, matrix3x4_expected);
    } else if(AREA(4, 4, 3, 3)) {
        TEST_MATRIX(matrix3x3, matrix3x3_expected);
    } else if(AREA(4, 7, 3, 2)) {
        TEST_MATRIX(matrix3x2, matrix3x2_expected);
    }

    if(AREA(7, 0, 2, 4)) {
        TEST_MATRIX(matrix2x4, matrix2x4_expected);
    } else if(AREA(7, 4, 2, 3)) {
        TEST_MATRIX(matrix2x3, matrix2x3_expected);
    } else if(AREA(7, 7, 2, 2)) {
        TEST_MATRIX(matrix2x2, matrix2x2_expected);
    }

    TEST_NA;
}

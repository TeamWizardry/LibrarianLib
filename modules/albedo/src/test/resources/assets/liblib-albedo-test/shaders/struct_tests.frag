#version 150

struct Embedded {
    float embed;
};
struct Simple {
    float primitive;
    float[2] primitiveArray;
    Embedded embedded;
    Embedded[2] embeddedArray;
};

uniform Simple simple;
Simple simple_expected = Simple(
    1,
    float[2](2, 3),
    Embedded(4),
    Embedded[2](Embedded(5), Embedded(6))
);

uniform Simple[2] simpleArray;
Simple[2] simpleArray_expected = Simple[2](
    Simple(
        11,
        float[2](12, 13),
        Embedded(14),
        Embedded[2](Embedded(15), Embedded(16))
    ),
    Simple(
        21,
        float[2](22, 23),
        Embedded(24),
        Embedded[2](Embedded(25), Embedded(26))
    )
);

#include "gridutil.glsl"

ivec2 dimensions = ivec2(6, 4);
void main() {
    if(isBorder(dimensions, 32) || isInThickLine(dimensions, 16, 1).x || isInThickLine(dimensions, 16, 3).x || isInThickLine(dimensions, 16, 2).y) {
        TEST_BORDER;
    }
    AREA_SETUP(dimensions);

    if(AREA(0, 0, 1, 1)) {
        TEST_EQUAL(simple.primitive, simple_expected.primitive);
    } else if(AREA(1, 0, 2, 1)) {
        TEST_ARRAY(simple.primitiveArray, simple_expected.primitiveArray);
    } else if(AREA(0, 1, 1, 1)) {
        TEST_EQUAL(simple.embedded, simple_expected.embedded);
    } else if(AREA(1, 1, 2, 1)) {
        TEST_ARRAY(simple.embeddedArray, simple_expected.embeddedArray);
//        TEST_EQUAL(simple.embeddedArray[rel.x].embed, simple_expected.embeddedArray[rel.x].embed);
    }

    abs -= ivec2(0, 2);
    if(AREA(0, 0, 1, 1)) {
        TEST_EQUAL(simpleArray[0].primitive, simpleArray_expected[0].primitive);
    } else if(AREA(1, 0, 2, 1)) {
        TEST_ARRAY(simpleArray[0].primitiveArray, simpleArray_expected[0].primitiveArray);
    } else if(AREA(0, 1, 1, 1)) {
        TEST_EQUAL(simpleArray[0].embedded, simpleArray_expected[0].embedded);
    } else if(AREA(1, 1, 2, 1)) {
        TEST_ARRAY(simpleArray[0].embeddedArray, simpleArray_expected[0].embeddedArray);
    }

    abs -= ivec2(3, 0);
    if(AREA(0, 0, 1, 1)) {
        TEST_EQUAL(simpleArray[1].primitive, simpleArray_expected[1].primitive);
    } else if(AREA(1, 0, 2, 1)) {
        TEST_ARRAY(simpleArray[1].primitiveArray, simpleArray_expected[1].primitiveArray);
    } else if(AREA(0, 1, 1, 1)) {
        TEST_EQUAL(simpleArray[1].embedded, simpleArray_expected[1].embedded);
    } else if(AREA(1, 1, 2, 1)) {
        TEST_ARRAY(simpleArray[1].embeddedArray, simpleArray_expected[1].embeddedArray);
    }

    TEST_NA;
}

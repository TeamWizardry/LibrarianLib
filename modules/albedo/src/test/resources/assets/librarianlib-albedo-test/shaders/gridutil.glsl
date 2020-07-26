vec4 borderColor = vec4(0, 0, 0, 1);
vec4 successColor = vec4(0, 102, 0, 255) / 255.0;
vec4 failColor = vec4(153, 0, 0, 255) / 255.0;
vec4 naColor = vec4(64, 64, 64, 255) / 255.0;

#define TEST_BORDER gl_FragColor = borderColor; return
#define TEST_SUCCESS gl_FragColor = successColor; return
#define TEST_FAIL gl_FragColor = failColor; return
#define TEST_BOOL(x) if(x) { TEST_SUCCESS; } else { TEST_FAIL; }
#define TEST_NA gl_FragColor = naColor; return

// defines `ivec2 abs, rel` (cell is absolute, rel is relative to area)
#define AREA_SETUP(dimensions) ivec2 abs = cell(dimensions), rel
#define AREA(minX, minY, width, height) ( ((rel = abs - ivec2(minX, minY)) == vec2(0, 0) || true) && \
    rel.x >= 0 && rel.x < width && rel.y >= 0 && rel.y < height )

#define TEST_EQUAL(name, name_expected) TEST_BOOL(name == name_expected)
#define TEST_MATRIX(name, name_expected) TEST_BOOL(name[rel.x][rel.y] == name_expected[rel.x][rel.y])
#define TEST_VECTOR(name, name_expected) TEST_BOOL(name[rel.x] == name_expected[rel.x])
#define TEST_ARRAY(name, name_expected) TEST_BOOL(name[rel.x] == name_expected[rel.x])

vec2 absolute(ivec2 dimensions) {
    return gl_TexCoord[0].xy * vec2(dimensions);
}

ivec2 cell(ivec2 dimensions) {
    return ivec2(absolute(dimensions));
}

bool isBorder(ivec2 dimensions, float realCellSize) {
    vec2 a = mod(absolute(dimensions), 1);
    float edge = 0.5/realCellSize; // half a line on each side
    return a.x < edge || a.x > 1.0 - edge || a.y < edge || a.y > 1.0 - edge;
}

bvec2 isInThickLine(ivec2 dimensions, float realCellSize, int index) {
    vec2 a = absolute(dimensions) - vec2(index, index);
    float edge = 1.0/realCellSize;
    return bvec2(a.x < edge && a.x > -edge, a.y < edge && a.y > -edge);
}

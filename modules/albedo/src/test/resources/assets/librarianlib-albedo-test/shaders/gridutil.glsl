vec4 borderColor = vec4(0, 0, 0, 1);
vec4 successColor = vec4(16, 200, 0, 255) / 255.0;
vec4 failColor = vec4(230, 40, 0, 255) / 255.0;
vec4 naColor = vec4(1, 1, 1, 1);

#define TEST_BORDER gl_FragColor = borderColor; return
#define TEST_SUCCESS gl_FragColor = successColor; return
#define TEST_FAIL gl_FragColor = failColor; return
#define TEST_BOOL(x) if(x) { TEST_SUCCESS; } else { TEST_FAIL; }
#define TEST_NA gl_FragColor = naColor; return

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
    float edge = 1.0/realCellSize; // half a line on each side
    return bvec2(a.x < edge && a.x > -edge, a.y < edge && a.y > -edge);
}

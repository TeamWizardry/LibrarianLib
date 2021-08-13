#version 150
// from minecraft:shaders/include/light.glsl

#define MINECRAFT_LIGHT_POWER   (0.6)
#define MINECRAFT_AMBIENT_LIGHT (0.4)

float minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal) {
    lightDir0 = normalize(lightDir0);
    lightDir1 = normalize(lightDir1);
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    return lightAccum;
}

float minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, mat3 normalMatrix, vec3 normal) {
    return minecraft_mix_light(lightDir0, lightDir1, normalMatrix * normal);
}

vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}

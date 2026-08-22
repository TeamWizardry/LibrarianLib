plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    kotlin("kapt")
}

architectury {
    compileOnly()
}

dependencies {
    minecraft(getLibrary("platform_minecraft"))
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:${getVersion("platform_yarnMappings")}:v2")
        mappings(getLibrary("platform_yarnMappingsNeoforgePatch"))
    })
    compileOnly(getLibrary("platform_mixin"))
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly(getLibrary("platform_mixinExtras"))
    annotationProcessor(getLibrary("platform_mixinExtras"))
}

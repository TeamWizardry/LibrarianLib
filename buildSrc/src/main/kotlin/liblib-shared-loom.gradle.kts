plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    kotlin("kapt")
}

architectury {
    compileOnly()
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:${rootProject.property("yarn_mappings")}:v2")
        mappings("dev.architectury:yarn-mappings-patch-neoforge:${rootProject.property("yarn_mappings_patch_neoforge_version")}")
    })
    compileOnly("org.spongepowered:mixin:0.8.5")
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")
}

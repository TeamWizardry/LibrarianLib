import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("fabric-loom")
}

configure<LoomGradleExtensionAPI> {
    shareCaches()
}

dependencies {
    val minecraft_version: String by project
    val yarn_mappings: String by project
    val loader_version: String by project
    val fabric_version: String by project
    val fabric_kotlin_version: String by project
    "minecraft"("com.mojang:minecraft:$minecraft_version")
    "mappings"("net.fabricmc:yarn:$yarn_mappings:v2")
    "modApi"("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    "modApi"("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
    "modImplementation"("net.fabricmc:fabric-loader:$loader_version")
}

// The genSources task demands that loom be on the buildscript classpath. However, applying the plugin through buildSrc
// doesn't seem to do that. We manually add it to the classpath of the root project, so we only enable genSources for
// that project.
tasks.whenTaskAdded {
    if(name.startsWith("genSources") || name.startsWith("unpickJar"))
        enabled = project == project.rootProject
}

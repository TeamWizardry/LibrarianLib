import net.fabricmc.loom.LoomGradleExtension

plugins {
    id("fabric-loom")
}

configure<LoomGradleExtension> {
    shareCaches = true
}


dependencies {
    val minecraft_version: String by project
    val yarn_mappings: String by project
    val loader_version: String by project
    val fabric_version: String by project
    val fabric_kotlin_version: String by project
    "minecraft"("com.mojang:minecraft:$minecraft_version")
    "mappings"("net.fabricmc:yarn:$yarn_mappings:v2")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    "modImplementation"("net.fabricmc:fabric-language-kotlin:1.5.0+kotlin.1.4.31")
}

// The genSources task demands that loom be on the buildscript classpath. However, applying the plugin through buildSrc
// doesn't seem to do that. We manually add it to the classpath of the root project, so we only enable genSources for
// that project.
tasks.whenTaskAdded {
    if(name.startsWith("genSources"))
        enabled = project == project.rootProject
}

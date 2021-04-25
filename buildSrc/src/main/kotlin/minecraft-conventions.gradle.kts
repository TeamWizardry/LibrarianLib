import org.quiltmc.loom.LoomGradleExtension

plugins {
    id("org.quiltmc.loom")
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
    "mappings"("org.quiltmc:yarn:$yarn_mappings:v2")
    "modCompile"("org.quiltmc:quilt-loader:$loader_version")

    "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    "modImplementation"("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")
}

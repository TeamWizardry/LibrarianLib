plugins {
    id("fabric-loom")
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

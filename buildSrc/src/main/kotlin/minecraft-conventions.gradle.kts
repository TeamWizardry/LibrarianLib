import org.quiltmc.loom.LoomGradleExtension

plugins {
    id("org.quiltmc.loom")
}

configure<LoomGradleExtension> {
    shareCaches = true
}

configurations.named("compileClasspath") {
    exclude(module = "fabric-loader")
    exclude(group = "net.fabricmc", module = "fabric-loader-sat4j")
    exclude(group = "net.fabricmc", module = "tiny-mappings-parser")
    exclude(group = "net.fabricmc", module = "tiny-remapper")
    exclude(group = "net.fabricmc", module = "sponge-mixin")
    exclude(group = "net.fabricmc", module = "access-widener")
}
configurations.named("runtimeClasspath") {
    exclude(module = "fabric-loader")
    exclude(group = "net.fabricmc", module = "fabric-loader-sat4j")
    exclude(group = "net.fabricmc", module = "tiny-mappings-parser")
    exclude(group = "net.fabricmc", module = "tiny-remapper")
    exclude(group = "net.fabricmc", module = "sponge-mixin")
    exclude(group = "net.fabricmc", module = "access-widener")
}

dependencies {
    val minecraft_version: String by project
    val yarn_mappings: String by project
    val loader_version: String by project
    val fabric_version: String by project
    val fabric_kotlin_version: String by project
    "minecraft"("com.mojang:minecraft:$minecraft_version")
    "mappings"("org.quiltmc:yarn:$yarn_mappings:v2")
    implementation("org.quiltmc:quilt-json5:1.0.0-rc.3")
    implementation("org.quiltmc:quilt-loader-sat4j:2.3.5")
    implementation("org.quiltmc:tiny-mappings-parser:0.3.0")
    implementation("org.quiltmc:tiny-remapper:0.3.2")
    implementation("org.quiltmc:sponge-mixin:0.9.2+mixin.0.8.2") {
        exclude(module = "launchwrapper")
        exclude(module = "guava")
    }
    implementation("org.quiltmc:access-widener:1.0.2")
    "modCompile"("org.quiltmc:quilt-loader:$loader_version")

    "modImplementation"("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    "modImplementation"("org.quiltmc:fabric-language-kotlin:$fabric_kotlin_version.local")
}

// The genSources task demands that loom be on the buildscript classpath. However, applying the plugin through buildSrc
// doesn't seem to do that. We manually add it to the classpath of the root project, so we only enable genSources for
// that project.
tasks.whenTaskAdded {
    if(name.startsWith("genSources"))
        enabled = project == project.rootProject
}

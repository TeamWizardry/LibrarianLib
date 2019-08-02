val kotlin_version: String by settings
val forgegradle_version: String by settings

pluginManagement {
    repositories {
        maven {
            name = "forge"
            url = uri("https://files.minecraftforge.net/maven")
        }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) useVersion(kotlin_version)
            else when (requested.id.id) {
                "net.minecraftforge.gradle" -> useModule("net.minecraftforge.gradle:ForgeGradle:$forgegradle_version")
            }
        }
    }
}

rootProject.name = "librarianlib"

val modules = listOf(
    "core",
    "utilities",
    "particles",
    "xtemplatex" // marker for createModule task
).dropLast(1)

modules.forEach { name ->
    include(name)
    project(":$name").projectDir = rootDir.resolve("modules/$name")
}

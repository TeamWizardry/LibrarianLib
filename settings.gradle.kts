enableFeaturePreview("STABLE_PUBLISHING")

val kotlin_version: String by settings
val dokka_version: String by settings
val forgegradle_version: String by settings
val bintray_version: String by settings
val artifactory_version: String by settings
val abc_version: String by settings

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
                "net.minecraftforge.gradle.forge" -> useModule("net.minecraftforge.gradle:ForgeGradle:$forgegradle_version")
                "org.jetbrains.dokka" -> useVersion(dokka_version)
                "com.jfrog.bintray" -> useVersion(bintray_version)
                "com.jfrog.artifactory" -> useVersion(artifactory_version)
            }
        }
    }
}

rootProject.name = "LibrarianLib"
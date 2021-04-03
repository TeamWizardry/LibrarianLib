@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
}

apply<LibLibModulePlugin>()

dependencies {
    api(project(":core"))
    api(project(":mirage"))
    api(project(":scribe"))
    api("org.junit.jupiter:junit-jupiter-api:5.6.2")
    api("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    api("org.junit.platform:junit-platform-launcher:1.6.2")
//    devClasspath("org.junit.jupiter:junit-jupiter-api:5.6.2")
//    devClasspath("org.junit.jupiter:junit-jupiter-engine:5.6.2")
//    devClasspath("org.junit.platform:junit-platform-launcher:1.6.2")
}

val mod_version: String by project
tasks.named<ProcessResources>("processResources") {
    filesMatching("**/mods.toml") {
        filter(org.apache.tools.ant.filters.ReplaceTokens::class, "tokens" to mapOf("version" to mod_version))
    }
}

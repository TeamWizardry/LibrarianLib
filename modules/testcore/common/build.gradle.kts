plugins {
    `liblib-module-common`
}

dependencies {
    modImplementation(libs.mods.architecturyApi)
}

tasks.withType<PublishToMavenRepository>().configureEach { enabled = false }
tasks.withType<PublishToMavenLocal>().configureEach { enabled = false }

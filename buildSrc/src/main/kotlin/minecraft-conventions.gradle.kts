import net.minecraftforge.gradle.userdev.UserDevExtension

plugins {
    id("net.minecraftforge.gradle")
}

val mc_mappings_channel: String by project
val mc_mappings_version: String by project
val mc_version: String by project
val forge_version: String by project
val kotlinforforge_version: String by project

configure<UserDevExtension> {
    mappings(mc_mappings_channel, mc_mappings_version)
}

dependencies {
    "minecraft"("net.minecraftforge:forge:$mc_version-$forge_version")

    implementation("thedarkcolour:kotlinforforge:$kotlinforforge_version")
}

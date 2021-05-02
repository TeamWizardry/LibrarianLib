@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
    `testmod-conventions`
    `java-library`
}

group = "com.teamwizardry.librarianlib"
version = "0.0.0"

dependencies {
    api(project(":core"))
    api("org.junit.jupiter:junit-jupiter-api:5.6.2")
    api("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    api("org.junit.platform:junit-platform-launcher:1.6.2")
    modImplementation("net.devtech:arrp:0.3.11")
}

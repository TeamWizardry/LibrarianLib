@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
}

configurations.create("devClasspath")

dependencies {
    api(project(":core"))
    api(project(":mirage"))
    api(project(":scribe"))
    api("org.junit.jupiter:junit-jupiter-api:5.6.2")
    api("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    api("org.junit.platform:junit-platform-launcher:1.6.2")
    "devClasspath"("org.junit.jupiter:junit-jupiter-api:5.6.2")
    "devClasspath"("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    "devClasspath"("org.junit.platform:junit-platform-launcher:1.6.2")
}

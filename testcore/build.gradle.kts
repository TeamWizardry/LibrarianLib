@file:Suppress("UnstableApiUsage")

plugins {
    `minecraft-conventions`
    `kotlin-conventions`
    `java-library`
}

dependencies.attributesSchema {
    attribute(LibLibAttributes.Target.attribute) {
        compatibilityRules.add(LibLibAttributes.Rules.optional())
    }
}

configurations {
    create("devClasspath") {
        isCanBeConsumed = true
        isCanBeResolved = false
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.devClasspath)
    }
    // consumers
    listOf(compileClasspath, runtimeClasspath, testCompileClasspath, testRuntimeClasspath).forEach {
        it {
            attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.internal)
        }
    }
    // provider
    apiElements {
        attributes.attribute(LibLibAttributes.Target.attribute, LibLibAttributes.Target.internal)
    }
}

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

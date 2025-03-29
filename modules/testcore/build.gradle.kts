
plugins {
    `module-conventions`
}

module {
    displayName = "Test Core"
    description = "A base framework for creating internal liblib tests"
    shadow("org.junit")
}

configureFabricModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.LLTestCoreCommon")
    mixin("ll/testcore/testcore.mixins.json")
}

configureFabricTestModJson {
    entrypoint("main", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.test.LLTestCoreTestCommon")
    entrypoint("client", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.test.LLTestCoreTestClient")
    entrypoint("server", adapter = "kotlin", value = "com.teamwizardry.librarianlib.testcore.test.LLTestCoreTestServer")
}


dependencies {
    liblib(project(":core"))

    api("org.junit.jupiter:junit-jupiter-api:${project.property("junit_version")}")
    api("org.junit.jupiter:junit-jupiter-engine:${project.property("junit_version")}")
    api("org.junit.platform:junit-platform-launcher:${project.property("junit_platform_version")}")
    shade("org.junit.jupiter:junit-jupiter-api:${project.property("junit_version")}")
    shade("org.junit.jupiter:junit-jupiter-engine:${project.property("junit_version")}")
    shade("org.junit.platform:junit-platform-launcher:${project.property("junit_platform_version")}")

    include("pers.solid:brrp-fabric:${project.property("brrp_version")}") {
        isTransitive = false
    }
    modImplementation("pers.solid:brrp-fabric:${project.property("brrp_version")}") {
        isTransitive = false
    }
}

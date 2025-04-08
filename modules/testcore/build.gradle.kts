plugins {
    `liblib-module-root`
}

module {
    displayName = "Test Core"
    description = "A base framework for creating internal liblib tests"
    shadowPackages("org.junit")
    moduleDependencies("core")
}

dependencies {
    shade("org.junit.jupiter:junit-jupiter-api:${project.property("junit_version")}")
    shade("org.junit.jupiter:junit-jupiter-engine:${project.property("junit_version")}")
    shade("org.junit.platform:junit-platform-engine:${project.property("junit_platform_version")}")
    shade("org.junit.platform:junit-platform-launcher:${project.property("junit_platform_version")}")
    includeFabric("pers.solid:brrp-fabric:${project.property("brrp_version")}") { isTransitive = false }
}

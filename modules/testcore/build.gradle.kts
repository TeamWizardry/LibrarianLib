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
    transitiveShade("org.junit.jupiter:junit-jupiter-api:${project.property("junit_version")}")
    transitiveShade("org.junit.jupiter:junit-jupiter-engine:${project.property("junit_version")}")
    transitiveShade("org.junit.platform:junit-platform-launcher:${project.property("junit_platform_version")}")
}

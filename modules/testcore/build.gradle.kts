plugins {
    `liblib-module-root`
}

module {
    displayName = "Test Core"
    description = "A base framework for creating internal liblib tests"
    shadowPackages("org.junit")
    shadowPackages("org.opentest4j")
    moduleDependencies("core")
}

dependencies {
    transitiveShade(libs.modules.testcore.junitApi)
    transitiveShade(libs.modules.testcore.junitEngine)
    transitiveShade(libs.modules.testcore.junitPlatform)
}

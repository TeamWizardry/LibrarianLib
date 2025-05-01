plugins {
    `liblib-module-root`
}

module {
    displayName = "Glitter"
    description = "High-performance particle systems"
    shadowPackages("org.magicwerk.brownies")
    moduleDependencies("core", "etcetera", "albedo")
}

dependencies {
    shade("org.magicwerk:brownies-collections:0.9.13")
}

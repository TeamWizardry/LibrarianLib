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
    shade(libs.modules.glitter.browniesCollections)
}

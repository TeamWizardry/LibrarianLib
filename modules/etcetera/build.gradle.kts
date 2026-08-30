plugins {
    `liblib-module-root`
}

module {
    displayName = "Etcetera"
    description = "Minor utilities that don't warrant their own modules"
    moduleDependencies("core")
}

dependencies {
    // forge needs to use yarn at compile time but bundle neoforge, so it's handled in the subproject
    includeFabric(libs.modules.etcetera.sableCompanionYarn)
}
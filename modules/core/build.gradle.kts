plugins {
    `liblib-module-root`
}

module {
    displayName = "Core"
    description = "Core classes used by the other LibrarianLib modules"
    shadowPackages("dev.thecodewarrior.mirror")
}

dependencies {
    shade(libs.modules.core.mirror)
}

plugins {
    `liblib-module-root`
}

module {
    displayName = "Core"
    description = "Core classes used by the other LibrarianLib modules"
    shadowPackages("dev.thecodewarrior.mirror")
}

dependencies {
    shade("dev.thecodewarrior:mirror:1.0.0-beta.4")
}

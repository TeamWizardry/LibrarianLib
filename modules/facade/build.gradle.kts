plugins {
    `liblib-module-root`
}

module {
    displayName = "Facade"
    description = "A feature-rich, flexible GUI framework"
    shadowPackages("dev.thecodewarrior.bitfont")
    shadowPackages("org.msgpack")
    moduleDependencies("core", "mosaic", "albedo", "etcetera")
}

val bitfont_version: String by project

dependencies {
    shade("dev.thecodewarrior:bitfont:$bitfont_version")
    shade("org.msgpack:msgpack-core:0.8.16")
}

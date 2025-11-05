plugins {
    `liblib-module-root`
}

module {
    displayName = "Facade"
    description = "A feature-rich, flexible GUI framework"
    shadowPackages("dev.thecodewarrior.bitfont")
    moduleDependencies("core", "mosaic", "albedo", "etcetera")
}

val bitfont_version: String by project

dependencies {
    shade("dev.thecodewarrior:bitfont:$bitfont_version")
    includeFabric("org.msgpack:msgpack-core:0.8.16")
    includeNeoForge("org.msgpack:msgpack-core:0.8.16")
}

rootProject.name = "librarianlib"

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("modules/$name")
}

includeModule("albedo")
includeModule("core")
includeModule("courier")
includeModule("etcetera")
includeModule("facade")
//includeModule("foundation")
includeModule("glitter")
includeModule("mosaic")
includeModule("scribe")

include("testcore")
include("runtime")
include("dist")

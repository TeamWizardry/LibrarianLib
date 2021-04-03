rootProject.name = "librarianlib"

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = java.io.File(rootDir.path + "/modules/" + name)
}

include("runtime")
include("dist")
include("testcore")
includeModule("core")
includeModule("courier")
includeModule("etcetera")
includeModule("facade")
includeModule("foundation")
includeModule("glitter")
includeModule("lieutenant")
includeModule("mirage")
includeModule("mosaic")
includeModule("scribe")
//includeModule("testbase")
includeModule("albedo")

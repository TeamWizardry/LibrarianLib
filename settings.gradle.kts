rootProject.name = "librarianlib"

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = java.io.File(rootDir.path + "/modules/" + name)
}

include("runtime")
include("dist")
//includeModule("lieutenant")
includeModule("core")
//includeModule("courier")
//includeModule("etcetera")
//includeModule("facade")
//includeModule("foundation")
//includeModule("glitter")
//includeModule("mirage")
//includeModule("mosaic")
//includeModule("prism")
//includeModule("testbase")
//includeModule("albedo")

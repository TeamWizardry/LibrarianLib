rootProject.name = "librarianlib"

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("modules/$name")
}

include("testcore")
includeModule("albedo")
includeModule("core")
//includeModule("courier")
includeModule("etcetera")
//includeModule("facade")
//includeModule("foundation")
includeModule("glitter")
includeModule("mosaic")
includeModule("scribe")

include("zz_runtime")
project(":zz_runtime").projectDir = rootDir.resolve("runtime")

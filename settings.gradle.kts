rootProject.name = "librarianlib"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://files.minecraftforge.net/maven/") }
    }
}

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("modules/$name")
    include("$name:common")
    project(":$name:common").projectDir = rootDir.resolve("modules/$name/common")
    include("$name:fabric")
    project(":$name:fabric").projectDir = rootDir.resolve("modules/$name/fabric")
    include("$name:neoforge")
    project(":$name:neoforge").projectDir = rootDir.resolve("modules/$name/neoforge")
    include("$name:testmod")
    project(":$name:testmod").projectDir = rootDir.resolve("modules/$name/testmod")
}

includeModule("albedo")
includeModule("core")
////includeModule("courier")
includeModule("etcetera")
////includeModule("facade")
//////includeModule("foundation")
includeModule("glitter")
includeModule("mosaic")
////includeModule("scribe")
includeModule("testcore")

include("runtime:fabric")
include("runtime:neoforge")

include("dist:fabric")
include("dist:neoforge")

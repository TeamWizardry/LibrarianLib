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

fun includeWithDir(name: String, dir: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve(dir)
}
fun includeModule(name: String) {
    includeWithDir(name, "modules/$name")
    includeWithDir("$name:common", "modules/$name/common")
    includeWithDir("$name:fabric", "modules/$name/fabric")
    includeWithDir("$name:neoforge", "modules/$name/neoforge")
    includeWithDir("$name:testmod", "modules/$name/testmod")
}

includeModule("albedo")
includeModule("core")
////includeModule("courier")
includeModule("etcetera")
////includeModule("facade")
//////includeModule("foundation")
includeModule("glitter")
includeModule("mosaic")
includeModule("scribe")
includeWithDir("scribe:annotations", "modules/scribe/annotations")
includeWithDir("scribe:processor", "modules/scribe/processor")
includeModule("testcore")

include("runtime:fabric")
include("runtime:neoforge")

include("dist:fabric")
include("dist:neoforge")

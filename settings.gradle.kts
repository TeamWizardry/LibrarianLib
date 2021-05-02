rootProject.name = "librarianlib"

fun includeModule(name: String) {
    include(name)
    project(":$name").projectDir = rootDir.resolve("modules/$name")
}

include("runtime")
include("testcore")
//includeModule("albedo")
includeModule("core")
//includeModule("courier")
includeModule("etcetera")
//includeModule("facade")
//includeModule("foundation")
//includeModule("glitter")
//includeModule("lieutenant")
//includeModule("mirage")
includeModule("mosaic")
//includeModule("scribe")

// This is absolutely disgusting, but ForgeGradle has forced my hand here. Even though the `RunConfig`'s `ModConfig`
// has no reason to need the `sourceSets` immediately, (the only place they're used is when creating IDE run
// configurations) it still requires them to be resolved at configure time, instead of using deferred configuration
// like the rest of gradle does.
//
// https://github.com/MinecraftForge/ForgeGradle/blob/83993e9/src/common/java/net/minecraftforge/gradle/common/util/ModConfig.java#L101-L108
//
// This wasn't a problem until I renamed the `prism` module to `scribe`. Because, you see, `s` comes after `r` in the
// alphabet, which means it gets configured later, which means when the `runtime` project is configured the `scribe`
// project isn't.
//
// So in summary, fuck Lex for making me do this shit. I would create a pull request if I knew it would actually be
// looked at sometime in the next two years.
//include("zzz:runtime")
//project(":zzz:runtime").projectDir = rootDir.resolve("runtime")

// Due to another issue in ForgeGradle I'm not allowed to depend on tasks lazily
// https://github.com/MinecraftForge/ForgeGradle/blob/6639464/src/userdev/java/net/minecraftforge/gradle/userdev/MinecraftUserRepo.java#L285
//
//include("zzz:librarianlib")
//project(":zzz:librarianlib").projectDir = rootDir.resolve("dist")

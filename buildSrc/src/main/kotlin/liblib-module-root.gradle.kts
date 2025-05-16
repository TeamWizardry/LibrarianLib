import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

plugins {
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()

configurations {
    val shade = create("shade") {
        description = "Dependencies to be shaded into the module"
        isTransitive = false
        canBe(consumed = true, resolved = false)
    }
    val transitiveShade = create("transitiveShade") {
        description = "Dependencies to be shaded into the module"
        isTransitive = true
        canBe(consumed = true, resolved = false)
    }
    create("includeFabric") {
        description = "Fabric mods to be included as jar-in-jar deps"
        canBe(consumed = true, resolved = false)
    }
    create("includeNeoForge") {
        description = "NeoForge mods to be included as jar-in-jar deps"
        canBe(consumed = true, resolved = false)
    }
}
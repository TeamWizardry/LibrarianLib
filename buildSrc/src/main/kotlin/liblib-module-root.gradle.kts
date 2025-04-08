import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

plugins {
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()

configurations {
    create("shade") {
        description = "Dependencies to be shaded into the module"
        canBe(consumed = true, resolved = false)
    }
    create("includeFabric") {
        description = "Fabric mods to be included as jar-in-jar deps"
        canBe(consumed = true, resolved = false)
    }
}
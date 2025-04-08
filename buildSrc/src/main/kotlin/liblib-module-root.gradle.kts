import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

plugins {
}

apply<LibLibModulePlugin>()
val module = the<ModuleExtension>()

configurations {
    create("shade") {
        canBe(consumed = true, resolved = false)
    }
}
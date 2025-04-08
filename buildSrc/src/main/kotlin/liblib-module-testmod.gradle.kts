import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.getByType

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
    id("com.github.johnrengelman.shadow")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

architectury {
    common(commonConfig.platforms)
}

configurations {
}

dependencies {
    // Architectury API. This is optional, and you can comment it out if you don't need it.
    modImplementation("dev.architectury:architectury:${rootProject.property("architectury_api_version")}")

    implementation(project(path = module.commonPath, configuration = "namedElements"))// { isTransitive = false }
    //implementation(project(path = ":testcore:common", configuration = "namedElements"))// { isTransitive = false }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf()
    archiveClassifier = "dev-shadow"

    commonConfig.shadowRules {
        relocate(it.from, it.to)
    }
}

import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

configure<KotlinProjectExtension> {
    explicitApi()
}

architectury {
    common(commonConfig.platforms)
}

loom {
    mixin.defaultRefmapName.set("ll/${module.name}/${module.name}-refmap.json")
}

dependencies {
    api(project(path = module.path, configuration = "shade"))
    api(project(path = module.path, configuration = "transitiveShade"))
    module.dependencies {
        api(project(it.commonPath, configuration = "namedElements"))
    }
}

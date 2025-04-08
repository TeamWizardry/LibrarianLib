import gradle.kotlin.dsl.accessors._3ad33576735bd3c2f3bc8765e93a6b18.loom
import org.gradle.kotlin.dsl.getByType

plugins {
    id("liblib-shared-langs")
    id("liblib-shared-loom")
}

val module = parent!!.extensions.getByType<ModuleExtension>()

architectury {
    common(commonConfig.platforms)
}

loom {
    mixin.defaultRefmapName.set("ll/${module.name}/${module.name}-refmap.json")
}

dependencies {
    api(project(path = module.path, configuration = "shade"))
    module.dependencies {
        api(project(it.commonPath, configuration = "namedElements"))
    }
}

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations

plugins {
    `module-conventions`
    `mixin-conventions`
    `publish-conventions`
}

module {
    shadow("dev.thecodewarrior.mirror")
}
val commonConfig = rootProject.the<CommonConfigExtension>()

dependencies {
    shade("dev.thecodewarrior.mirror:mirror:1.0.0b1")
    testApi(project(":testcore"))
}

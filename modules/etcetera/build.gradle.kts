import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations

plugins {
    `module-conventions`
}

val commonConfig = rootProject.the<CommonConfigExtension>()

dependencies {
    liblib(project(":core"))
//    testApi(project(":testcore"))
}

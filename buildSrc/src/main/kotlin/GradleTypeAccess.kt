import org.gradle.api.Project
import org.gradle.api.Named
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler

typealias CommonConfigPlugin = com.teamwizardry.gradle.CommonConfigPlugin
typealias CommonConfigExtension = com.teamwizardry.gradle.CommonConfigExtension
typealias LibLibModulePlugin = com.teamwizardry.gradle.module.LibLibModulePlugin
typealias ModuleExtension = com.teamwizardry.gradle.module.ModuleExtension
typealias ModPublishingPlugin = com.teamwizardry.gradle.publish.ModPublishingPlugin
typealias ModPublishingExtension = com.teamwizardry.gradle.publish.ModPublishingExtension

typealias GenerateCoremodsJson = com.teamwizardry.gradle.task.GenerateCoremodsJson
typealias GenerateMixinConnector = com.teamwizardry.gradle.task.GenerateMixinConnector
typealias GenerateModInfo = com.teamwizardry.gradle.task.GenerateModInfo
typealias ValidateMixinApplication = com.teamwizardry.gradle.task.ValidateMixinApplication


inline fun <reified T: Named> Project.namedAttribute(value: String): T = objects.named(T::class.java, value)

fun Configuration.canBe(consumed: Boolean, resolved: Boolean) {
    isCanBeConsumed = consumed
    isCanBeResolved = resolved
}

/**
 * Add a dependency to the `shade` configuration. This automatically disables transitive dependencies.
 */
fun DependencyHandler.shade(dependencyNotation: Any): Dependency? {
    val dep = add("shade", dependencyNotation)
    if(dep is ModuleDependency) {
        dep.isTransitive = false
    }
    return dep
}

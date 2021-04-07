import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import com.teamwizardry.gradle.ModuleInfo

typealias CommonConfigPlugin = com.teamwizardry.gradle.CommonConfigPlugin
typealias CommonConfigExtension = com.teamwizardry.gradle.CommonConfigExtension
typealias LibLibModulePlugin = com.teamwizardry.gradle.module.LibLibModulePlugin
typealias ModuleExtension = com.teamwizardry.gradle.module.ModuleExtension

typealias GenerateCoremodsJson = com.teamwizardry.gradle.task.GenerateCoremodsJson
typealias GenerateMixinConnector = com.teamwizardry.gradle.task.GenerateMixinConnector
typealias GenerateModInfo = com.teamwizardry.gradle.task.GenerateModInfo
typealias ValidateMixinApplication = com.teamwizardry.gradle.task.ValidateMixinApplication

val Project.commonConfig: CommonConfigExtension
    get() = this.extensions.getByType()

val Project.liblibModules: NamedDomainObjectCollection<ModuleInfo>
    get() = this.commonConfig.modules

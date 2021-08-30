package com.teamwizardry.gradle.module

import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.ModuleInfo
import com.teamwizardry.gradle.CommonConfigExtension
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the

/**
 * Currently, the module defines:
 * - which modules it depends on, which are turned into `api` dependencies and may be used for introspection in the
 *   future
 * - what shadow relocation rules it requires. (these are sent back to the root common config to populate the main list,
 *   as well as notify any existing listeners of the new rule)
 */
open class ModuleExtension(private val ctx: DslContext) {
    private val commonConfig = ctx.project.rootProject.the<CommonConfigExtension>()

    private val _shadowRules = mutableListOf<ShadowRule>()

    val name: String = ctx.project.name
    val moduleInfo: ModuleInfo = commonConfig.modules[name]
    val component: AdhocComponentWithVariants
        get() = ctx.project.components.getByName("module") as AdhocComponentWithVariants

    var displayName: String = ""
    var description: String = ""

    val shadowRules: List<ShadowRule> = _shadowRules

    /**
     * Adds a shadow rule. The passed package will be relocated under the `ll` package.
     */
    fun shadow(pkg: String) {
        val rule = ShadowRule(pkg, "ll.$pkg")
        _shadowRules.add(rule)
        commonConfig.addShadowRule(rule)
    }

    init {
        ctx.project.configurations.register("liblib") {
            this.dependencies.all {
                if(this is ProjectDependency) {
                    moduleInfo.dependencies.add(commonConfig.modules[dependencyProject.name])
                }
            }
        }
    }
}

data class ShadowRule(val from: String, val to: String)


package com.teamwizardry.gradle.module

import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.ModuleInfo
import com.teamwizardry.gradle.CommonConfigExtension
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.project
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

    val name: String = ctx.project.name
    val moduleInfo: ModuleInfo = commonConfig.modules[name]
    val component: AdhocComponentWithVariants
        get() = ctx.project.components.getByName("module") as AdhocComponentWithVariants

    var displayName: String = ""
    var description: String = ""

    /**
     * Adds a shadow rule. The passed package will be relocated under the `ll` package.
     */
    fun shadowPackages(pkg: String) {
        commonConfig.addShadowRule(pkg)
    }

    /**
     * Adds dependencies on the given liblib modules
     */
    fun moduleDependencies(vararg modules: String) {
        ctx.project.dependencies {
            for (module in modules) {
                "liblib"(project(":${module}", configuration = "namedElements"))
            }
        }
        for (module in modules) {
            moduleInfo.dependencies.add(commonConfig.modules[module])
        }
    }

    init {
        ctx.project.configurations.register("liblib") {
            description = "Inter-module dependencies"

            isCanBeConsumed = false
            isCanBeResolved = false
        }
    }
}

data class ShadowRule(val from: String, val to: String)


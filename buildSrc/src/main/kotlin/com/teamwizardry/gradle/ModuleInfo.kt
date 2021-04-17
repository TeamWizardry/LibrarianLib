package com.teamwizardry.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.the
import com.teamwizardry.gradle.util.LiveCollection

open class ModuleInfo(val name: String, private val ctx: DslContext) {
    val path: String = ":$name"
    val project: Project = ctx.project.project(":$name")

    /**
     * The direct dependencies of this module. (this is populated by the module plugin)
     */
    val dependencies: LiveCollection<ModuleInfo> = LiveCollection(mutableSetOf())

    /**
     * The direct and transitive dependencies of this module
     */
    val allDependencies: LiveCollection<ModuleInfo> = LiveCollection(mutableSetOf())

    val testModid: String = "ll-$name-test"

    val mainSources: Property<SourceSet> = ctx.property() {
        project.the<SourceSetContainer>().getByName("main")
    }
    val testSources: Property<SourceSet> = ctx.property() {
        project.the<SourceSetContainer>().getByName("test")
    }

    init {
        dependencies { dep ->
            // add direct dependencies
            allDependencies.add(dep)
            // add all of those dependencies' direct and transitive dependencies
            dep.allDependencies {
                allDependencies.add(it)
            }
        }
    }
}

package com.teamwizardry.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.the

open class ModuleInfo(val name: String, private val ctx: DslContext) {
    val path: String = ":$name"
    val project: Project = ctx.project.project(":$name")

    val testModid: String = "ll-$name-test"

    val mainSources: Property<SourceSet> = ctx.property() {
        project.the<SourceSetContainer>().getByName("main")
    }
    val testSources: Property<SourceSet> = ctx.property() {
        project.the<SourceSetContainer>().getByName("test")
    }
}

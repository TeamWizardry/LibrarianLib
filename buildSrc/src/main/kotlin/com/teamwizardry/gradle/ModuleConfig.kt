package com.teamwizardry.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import com.teamwizardry.gradle.util.DslContext
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.the

open class ModuleConfig(val name: String, private val ctx: DslContext) {
    val testModid: Property<String> = ctx.property("ll-$name-test")
    val project: Property<Project> = ctx.property() { ctx.project.project(":$name")  }

    val mainSources: Property<SourceSet> = ctx.property() {
        project.get().the<SourceSetContainer>().getByName("main")
    }
    val testSources: Property<SourceSet> = ctx.property() {
        project.get().the<SourceSetContainer>().getByName("test")
    }
}

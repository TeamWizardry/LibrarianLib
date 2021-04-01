package com.teamwizardry.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import com.teamwizardry.gradle.util.DslContext

open class ModuleConfig(val name: String, val ctx: DslContext) {
    val modid: Property<String> = ctx.property("ll-$name")
    val testModid: Property<String> = ctx.property("ll-$name-test")
    val project: Property<Project> = ctx.property() { ctx.project.project(":$name")  }

    /**
     * Whether this module is internal and shouldn't be published
     */
    val internal: Property<Boolean> = ctx.property(false)
}

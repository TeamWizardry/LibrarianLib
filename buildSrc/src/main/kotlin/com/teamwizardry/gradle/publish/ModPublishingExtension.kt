package com.teamwizardry.gradle.publish

import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.util.LiveCollection
import com.teamwizardry.gradle.CommonConfigExtension
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.kotlin.dsl.the

open class ModPublishingExtension(private val ctx: DslContext) {
    val component: AdhocComponentWithVariants
        get() = ctx.project.components.getByName("mod") as AdhocComponentWithVariants
}


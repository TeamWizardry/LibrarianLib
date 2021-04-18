package com.teamwizardry.gradle.publish

import com.teamwizardry.gradle.util.DslContext
import com.teamwizardry.gradle.util.LiveCollection
import com.teamwizardry.gradle.CommonConfigExtension
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.kotlin.dsl.the

open class ModPublishingExtension(private val ctx: DslContext) {
    val artifactId: Property<String> = ctx.property()
    val pomName: Property<String> = ctx.property()
    val pomDescription: Property<String> = ctx.property()
    val developers: LiveCollection<MavenPomDeveloper.() -> Unit> = LiveCollection(mutableListOf())

    fun developer(dev: MavenPomDeveloper.() -> Unit) {
        developers.add(dev)
    }
}


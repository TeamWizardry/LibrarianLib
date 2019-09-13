@file:Suppress("UnstableApiUsage")

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.CopySpec
import org.gradle.jvm.tasks.Jar
import java.io.File

class ContainedDependencies(private var jar: Jar) {
    private val spec = jar.project.copySpec()

    init {
        jar.with(spec)
        spec.into("META-INF/libraries/")
    }

    fun add(configuration: Configuration) {
        configuration.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
            val id = artifact.moduleVersion.id
            add("${id.group}:${id.name}:${id.version}", artifact.file)
        }
    }

    fun add(artifactName: String, file: File) {
        val temp = jar.temporaryDir.resolve(file.name + ".meta")
        temp.writeText("Maven-Artifact: $artifactName")

        spec.from(file)
        spec.from(temp)

        val existing = jar.manifest.attributes["ContainedDeps"]
        if(existing !is String)
            jar.manifest.attributes["ContainedDeps"] = file.name
        else
            jar.manifest.attributes["ContainedDeps"] = "$existing ${file.name}"
    }

    inline operator fun invoke(config: ContainedDependencies.() -> Unit): ContainedDependencies {
        this.config()
        return this
    }
}

inline fun Jar.containedDeps(config: ContainedDependencies.() -> Unit = {}): ContainedDependencies {
    val deps = ContainedDependencies(this)
    deps.config()
    return deps
}

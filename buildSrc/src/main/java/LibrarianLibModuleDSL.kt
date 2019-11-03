import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

/**
 * Creates a dependency on the passed module (e.g. "core", "utilities", "particles")
 */
fun DependencyHandler.librarianlib(name: String): Dependency? =
    add("librarianlib", project(":$name"))

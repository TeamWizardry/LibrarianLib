import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

class LibrarianLibModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("LibrarianLibModule", LibrarianLibModule::class.java, target)
        target.extensions.getByType(LibrarianLibModule::class.java)

        val liblibConfiguration = target.configurations.create("librarianlib")
        liblibConfiguration.extendsFrom(target.configurations["compileOnly"])
    }
}

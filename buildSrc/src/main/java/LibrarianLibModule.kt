import org.gradle.api.Project

open class LibrarianLibModule(private val project: Project) {
    /**
     * The name of the liblib module. e.g. "core", "utilities", "particles".
     * Defaults to the project name.
     */
    var name: String = project.name
    var dependencies: List<String> = listOf()

    // ==================================================== DSL ===================================================== //



    // ================================================== SUPPORT =================================================== //

}
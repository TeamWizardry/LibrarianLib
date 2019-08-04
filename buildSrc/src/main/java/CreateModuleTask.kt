import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import kotlin.streams.toList

open class CreateModuleTask: DefaultTask() {
    @set:Option(option = "name", description = "The UpperCamelCase name of the module")
    var moduleName: String? = null

    @set:Option(option = "human-name", description = "The Title Case human readable name of the module. " +
        "Defaults to the split camel case module name")
    var humanName: String? = null

    @set:Option(option = "description", description = "The description for this module's mods.toml")
    var moduleDescription: String? = null

    @TaskAction
    fun create() {
        val moduleName = this.moduleName ?: run {
            throw IllegalArgumentException("No module name specified")
        }

        val transformer = NameTransformer(moduleName, humanName, moduleDescription)
        logger.lifecycle("Creating \"$humanName\" module")
        logger.lifecycle("$transformer")

        val templateSource = project.rootDir.resolve("buildSrc/moduleTemplate").toPath()
        val templateDestination = project.rootDir.resolve("modules/${transformer.lowercase}").toPath()
        if(Files.exists(templateDestination)) {
            throw IllegalArgumentException("Module ${transformer.lowercase} already exists")
        }

        Files.walk(templateSource).forEach { source ->
            val dest = templateDestination.resolve(templateSource.relativize(source))
            if(Files.isRegularFile(source)) {
                val text = source.toFile().readText()
                dest.toFile().writeText(transformer.transform(text))
            } else if(Files.isDirectory(source)) {
                Files.createDirectory(dest)
            }
        }
        Files.walk(templateDestination).toList().reversed().forEach { file ->
            val fileName = file.fileName.toString()
            val transformedName = transformer.transform(fileName)
            if(fileName != transformedName)
                Files.move(file, file.parent.resolve(transformedName))
        }

        val settings = project.rootDir.resolve("settings.gradle.kts")
        val settingsText = settings.readText()
        settings.writeText(settingsText.replace("(\\s*)\"xtemplatex\"".toRegex(), "$1\"${transformer.lowercase}\",$0"))
    }

    /**
     * ```
     * XTemplateX = UpperCamelCase name
     * xTemplatex = camelCase name
     * xtemplatex = lowercase name
     * utemplateu = lowercase human-readable name
     * UTemplateU = Title Case Human-Readable name
     * UDescriptionU = Description
     * ```
     */
    private class NameTransformer(name: String, humanName: String?, description: String?) {
        val upperCamelCase: String
        val camelCase: String
        val lowercase: String
        val lowercaseHuman: String
        val titlecaseHuman: String
        val description: String
        val mappings: Map<String, String>

        init {
            if(name.isEmpty()) {
                throw IllegalArgumentException("Module name is empty")
            }
            if(name.contains("\\s".toRegex())) {
                throw IllegalArgumentException("Module name `$name` contains whitespace")
            }

            upperCamelCase = name
            camelCase = name[0].toLowerCase() + name.substring(1)
            lowercase = name.toLowerCase(Locale.ROOT)

            titlecaseHuman = humanName ?: run {
                name.split("(?<=[a-z])(?=[A-Z])".toRegex()).joinToString(" ")
            }
            lowercaseHuman = titlecaseHuman.toLowerCase(Locale.ROOT)

            this.description = description ?: "The $titlecaseHuman module from LibrarianLib"

            mappings = mapOf(
                "XTemplateX" to this.upperCamelCase,
                "xTemplatex" to this.camelCase,
                "xtemplatex" to this.lowercase,
                "utemplateu" to this.lowercaseHuman,
                "UTemplateU" to this.titlecaseHuman,
                "UDescriptionU" to this.description
            )
        }

        fun transform(value: String): String {
            return value.replace(mappings.keys.joinToString("|").toRegex()) { match ->
                mappings[match.value] ?: match.value
            }
        }

        override fun toString(): String {
            return """
                Identifier names:
                    UpperCamelCase: `$upperCamelCase`
                    camelCase: `$camelCase`
                    lowercase: `$lowercase`
                Human-readable names:
                    lowercase: `$lowercaseHuman`
                    titlecase: `$titlecaseHuman`
                Description: `$description`
            """.trimIndent()
        }
    }
}
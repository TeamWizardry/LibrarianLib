package com.teamwizardry.librarianlib.scribe.test.util

import com.teamwizardry.librarianlib.scribe.AutoCodec
import com.teamwizardry.librarianlib.scribe.processor.ScribeProcessorProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.tschuchort.compiletesting.useKsp2
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.fail
import java.io.IOException
import kotlin.reflect.KClass

class ScribeTestHelper {
    private val sourceFiles = mutableListOf<SourceFile>()
    private val imports = mutableSetOf<String>()

    init {
        addStandardImports()
    }

    private fun addStandardImports() {
        addImport<AutoCodec>()
    }

    /**
     * Adds the provided imports
     */
    fun addImports(vararg imports: String): ScribeTestHelper {
        this.imports.addAll(imports)
        return this
    }

    /**
     * Adds an import for the specified type
     */
    inline fun <reified T> addImport(): ScribeTestHelper = T::class.qualifiedName?.let { addImports(it) }
        ?: throw IllegalArgumentException("Type has no qualified name?!")

    /**
     * Adds a Kotlin source file.
     *
     * The contained file will automatically include the standard imports: [addStandardImports].
     *
     * IntelliJ flags imports in the code fragment as errors ([KTIJ-32613](https://youtrack.jetbrains.com/issue/KTIJ-32613)),
     * so until that issue is resolved, Kotlin code *must* use [addImport] instead of specifying them inline.
     *
     * @param name The file name/path, excluding the `.kt` extension
     * @param contents The file contents, excluding package and import statements
     */
    fun kotlin(name: String, @Language("kotlin") contents: String): SourceFile {
        return addFile(name, ".kt", false, contents)
    }

    /**
     * Adds a Java source file.
     *
     * The contained file will automatically include the standard imports: [addStandardImports].
     *
     * @param name The file name/path, excluding the `.java` extension
     * @param contents The file contents, excluding package statement
     */
    fun java(name: String, @Language("java") contents: String): SourceFile {
        return addFile(name, ".java", true, contents)
    }

    private fun addFile(name: String, extension: String, semicolons: Boolean, contents: String): SourceFile {
        require('.' !in name) { "Illegal file path. Should not include file extension and can not contain periods: '$name'"}
        val fullPath = name + extension

        val semi = if (semicolons) ";" else ""
        var fullContents = ""
        if ('/' in name) {
            fullContents += "package ${name.split('/').dropLast(1).joinToString(".")}$semi\n"
        }
        fullContents += imports.joinToString("") { "import $it$semi\n" }
        fullContents += contents.trimIndent()

        val sourceFile = SourceFile.new(fullPath, fullContents)
        sourceFiles.add(sourceFile)
        return sourceFile
    }

    fun compile(): TestCompilationResult {
        val compilation = KotlinCompilation().apply {
            sources = sourceFiles

            // values from `liblib-shared-langs.gradle.kts`:
            // from `tasks.withType<KotlinCompile> { ... }`
            languageVersion = "2.0"
            apiVersion = "2.0"
            jvmTarget = "21"
            javaParameters = true
            jvmDefault = "all"
            // from `ksp { ... }`
            useKsp2()

            symbolProcessorProviders.add(ScribeProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }

        return TestCompilationResult(compilation, compilation.compile())
    }

    class TestCompilationResult(val compilation: KotlinCompilation, val compilationResult: JvmCompilationResult) {
        fun assertSuccess(): TestCompilationResult {
            if (compilationResult.exitCode != KotlinCompilation.ExitCode.OK) {

                val failure = AssertionFailureBuilder.assertionFailure()
                    .message(compilationResult.exitCode.name)

                if (compilationResult.exitCode == KotlinCompilation.ExitCode.COMPILATION_ERROR) {
                    val errorStart = compilationResult.messages.indexOf("\ne:")
                    if (errorStart >= 0) {
                        val errorLines = compilationResult.messages.substring(errorStart + 1)
                        // logs include trailing newline, so no newline needed after it
                        failure.reason("\n---=== Compilation log ===---\n$errorLines---=== End compilation log ===---")
                    }
                }
                failure.buildAndThrow()
            }
            return this
        }

        fun getGeneratedFile(path: String): String {
            return try {
                compilation.kspSourcesDir.resolve("kotlin").resolve(path).readText()
            } catch (e: IOException) {
                fail("Generated file not found: $path", e)
            }
        }

        fun tryGetGeneratedFile(path: String): String? {
            return try {
                compilation.kspSourcesDir.resolve("kotlin").resolve(path).readText()
            } catch (e: IOException) {
                null
            }
        }

        fun getClass(name: String): KClass<*> = compilationResult.classLoader.loadClass(name).kotlin

    }
}
package com.teamwizardry.librarianlib.albedo.shader

import com.teamwizardry.librarianlib.albedo.LibLibAlbedo
import com.teamwizardry.librarianlib.albedo.ShaderCompilationException
import com.teamwizardry.librarianlib.core.util.extension
import com.teamwizardry.librarianlib.core.util.resolve
import com.teamwizardry.librarianlib.core.util.resolveSibling
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL20.*
import java.util.*

/**
 * A preprocessor that provides `#include` support. Include paths without a mod ID will be resolved in the current
 * file's mod. All paths are resolved inside the mod's `shaders` directory.
 *
 * This class automatically selects the maximum GLSL version used by any of the included files, and inserts `#line`
 * directives so each file has the correct line number. Pass the shader log through
 * [PreprocessorResult.replaceFilenames] to replace the generated file codes with the proper filenames.
 */
public object ShaderCompiler {

    @JvmStatic
    public fun compileShader(stage: Shader.Stage, shader: PreprocessorResult): Int {
        logger.debug("Compiling $stage shader ${shader.location}")
        val glShader = glCreateShader(stage.glConstant)
        if (glShader == 0)
            throw ShaderCompilationException("Could not create shader object")
        glShaderSource(glShader, shader.code)
        glCompileShader(glShader)

        val status = glGetShaderi(glShader, GL_COMPILE_STATUS)
        if (status == GL_FALSE) {
            val logLength = glGetShaderi(glShader, GL_INFO_LOG_LENGTH)
            var log = glGetShaderInfoLog(glShader, logLength)
            glDeleteShader(glShader)
            log = shader.replaceFilenames(log)

            logger.error("Error compiling $stage shader. Shader source text:\n${shader.code}\nOpenGL error log:\n$log")
            throw ShaderCompilationException("Error compiling $stage shader `${shader.location}`:\n$log")
        }

        return glShader
    }

    @JvmStatic
    public fun preprocessShader(location: Identifier, defines: List<String>, resourceManager: ResourceManager): PreprocessorResult {
        return preprocessShader(location, defines) {
            resourceManager.getResource(it).inputStream.bufferedReader().readText()
        }
    }

    @JvmStatic
    public fun preprocessShader(location: Identifier, defines: List<String>, reader: (Identifier) -> String): PreprocessorResult {
        val info = PreprocessorResult(location)
        val shaderText = readShader(info, reader, location)
        if (info.glslVersion < 0)
            throw ShaderCompilationException("No GLSL version found while preprocessing $location")

        val defineText = defines.joinToString("") { "#define $it 1\n" }
        info.code = "#version ${info.glslVersion}\n$defineText$shaderText"
        return info
    }

    public class PreprocessorResult(public val location: Identifier) {
        public var code: String = ""
        public val files: MutableMap<Identifier, Int> = mutableMapOf()
        public var glslVersion: Int = -1

        public fun replaceFilenames(log: String): String {
            var fixed = log
            files.forEach { (key, value) ->
                fixed =
                    fixed.replace(Regex("\\b$value\\b"), if (key.namespace != location.namespace) "$key" else key.path)
            }
            return fixed
        }
    }

    private fun readShader(
        result: PreprocessorResult,
        reader: (Identifier) -> String,
        file: Identifier,
        stack: LinkedList<Identifier> = LinkedList()
    ): String {
        if (file in stack) {
            val cycleString = stack.reversed().joinToString(" -> ") { if (it == file) "[$it" else "$it" } + " -> $file]"
            throw ShaderCompilationException("#include cycle: $cycleString")
        }
        stack.push(file)
        val sourceNumber = result.files.getOrPut(file) { BASE_SOURCE_NUMBER + result.files.size }
        val includeRegex = """^\s*#include\s*"\s*(?<relative>\S*)\s*"\s*$""".toRegex()

        val text = reader(file)
        var out = ""
        var lineNumber = 0
        for (line in text.lineSequence()) {
            lineNumber++
            if (lineNumber == 1 && "#version" !in text) {
                out += "#line 0 $sourceNumber // $file\n"
            }
            if ("#version" in line) {
                requireVersion(result, file, line)
                out += "//$line\n"
                out += "#line $lineNumber $sourceNumber // $file\n"
                continue
            }

            val includeMatch = includeRegex.matchEntire(line)
            if (includeMatch != null) {
                val includeName = includeMatch.groups["relative"]?.value!!
                val includeLocation = if (':' !in includeName) {
                    Identifier(file.namespace, "shaders/$includeName")
                } else {
                    Identifier(includeName.substringBefore(":"), "shaders/" + includeName.substringAfter(":"))
                }

                out += readShader(result, reader, includeLocation, stack)
                out += "\n#line $lineNumber $sourceNumber // $file\n"
            } else {
                out += "$line\n"
            }
        }

        stack.pop()

        return out
    }

    /**
     * Check the GLSL version directive, and increase our required version if necessary
     */
    private fun requireVersion(result: PreprocessorResult, file: Identifier, line: String) {
        val match = """^\s*#version\s+(\d+)\s*$""".toRegex().find(line) ?: return
        val version = match.groupValues[1].toInt()
        if (version > 150) // Mojang's shaders are all GLSL 1.50
            logger.warn(
                "Maximum recommended OpenGL version 3.2 (GLSL 150). " +
                        "Found `${match.value}` in $file while preprocessing ${result.location}"
            )
        if (version > result.glslVersion)
            result.glslVersion = version
    }

    /**
     * The base source string number. Sufficiently high that _hopefully_ a substitution in the error log will be
     * correct, sufficiently low so even a signed short won't overflow, and sufficiently different from the max signed
     * short value that anyone using that max value in their code won't have collisions
     */
    private const val BASE_SOURCE_NUMBER = 31500

    private val logger = LibLibAlbedo.makeLogger<ShaderCompiler>()
}

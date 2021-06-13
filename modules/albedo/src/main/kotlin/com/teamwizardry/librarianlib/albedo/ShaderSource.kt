package com.teamwizardry.librarianlib.albedo

import com.teamwizardry.librarianlib.core.util.resolve
import com.teamwizardry.librarianlib.core.util.resolveSibling
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.*

/**
 * A preprocessor that provides `#pragma include` support.
 *
 * Includes come in two varieties, those surrounded by quotes (`#pragma include "location"`) and those surrounded by
 * angle brackets (`#pragma include <location>`). The form in quotes is interpreted as a resource location or a
 * relative path, and the form in brackets is interpreted as a shader from the Albedo standard library. (Located in
 * `liblib-albedo:shaders/stdlib`)
 *
 * This class automatically selects the maximum GLSL version used by any of the included files, and inserts `#line`
 * directives so each file has the correct line number. Pass the shader log through [replaceFilenames] to replace the
 * generated file codes with the proper filenames.
 */
public class ShaderSource(public val location: Identifier, private val reader: (Identifier) -> String) {
    public constructor(resourceManager: ResourceManager, file: Identifier) : this(file, {
        resourceManager.getResource(it).inputStream.bufferedReader().readText()
    })

    private val files = mutableMapOf<Identifier, Int>()
    private var glslVersion: Int = -1
    public val source: String

    init {
        val shaderText = readShader(location)
        if(glslVersion < 0)
            throw ShaderCompilationException("No GLSL version found while preprocessing $location")
        source = "#version $glslVersion\n$shaderText"
    }

    public fun replaceFilenames(log: String): String {
        var fixed = log
        files.forEach { (key, value) ->
            fixed = fixed.replace(Regex("\\b$value\\b"), if (key.namespace != location.namespace) "$key" else key.path)
        }
        return fixed
    }

    private fun readShader(file: Identifier, stack: LinkedList<Identifier> = LinkedList()): String {
        if (file in stack) {
            val cycleString = stack.reversed().joinToString(" -> ") { if (it == file) "[$it" else "$it" } + " -> $file]"
            throw ShaderCompilationException("#pragma include cycle: $cycleString")
        }
        stack.push(file)
        val sourceNumber = files.getOrPut(file) { sourceNumberBase + files.size }
        val includeRegex = """^\s*#pragma\s+include\s*(?:<\s*(?<system>\S*)\s*>|"\s*(?<relative>\S*)\s*")\s*$""".toRegex()

        val text = reader(file)
        var out = ""
        var lineNumber = 0
        for(line in text.lineSequence()) {
            lineNumber++
            if (lineNumber == 1 && "#version" !in text) {
                out += "#line 0 $sourceNumber // $file\n"
            }
            if ("#version" in line) {
                requireVersion(file, line)
                out += "//$line\n"
                out += "#line $lineNumber $sourceNumber // $file\n"
                continue
            }

            val includeMatch = includeRegex.matchEntire(line)
            if (includeMatch != null) {
                val includeName = includeMatch.groups["system"]?.value ?: includeMatch.groups["relative"]?.value!!
                val includeLocation = if (':' !in includeName) {
                    file.resolveSibling(includeName)
                } else if(includeMatch.groups["system"] != null) {
                    standardRoot.resolve(includeName)
                } else {
                    Identifier(includeName)
                }

                out += readShader(includeLocation, stack)
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
    private fun requireVersion(file: Identifier, line: String) {
        val match = """^\s*#version\s+(\d+)\s*$""".toRegex().find(line) ?: return
        val version = match.groupValues[1].toInt()
        if (version > 410) // As of macOS 10.9 Apple supports up to GLSL 4.10.
            throw ShaderCompilationException("Maximum GLSL version supported by all platforms is 4.10. " +
                    "Found `${match.value}` in $file while preprocessing $location")
        if(version > glslVersion)
            glslVersion = version
    }

    private companion object {
        private val standardRoot = Identifier("liblib-albedo:shaders/stdlib")

        /**
         * The base source string number. Sufficiently high that _hopefully_ a substitution in the error log will be
         * correct, sufficiently low so even a signed short won't overflow, and sufficiently different from the max signed
         * short value that anyone using that max value in their code won't have collisions
         */
        private val sourceNumberBase = 31500
    }
}
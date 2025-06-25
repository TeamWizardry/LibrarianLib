package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName

sealed class CodecSource(val declarationName: String?, val matcher: CodecTypeMatcher) : Comparable<CodecSource> {
    abstract fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock

    fun matches(type: KSType): Boolean = matcher.matches(type)

    override fun compareTo(other: CodecSource): Int = this.matcher.compareTo(other.matcher)

    companion object {
        fun codecNotFoundError(name: String): CodeBlock {
            return CodeBlock.of("%M(%S)", CommonNames.resolutionError_codecNotFound, name)
        }

        fun unsupportedNullableGenericError(name: String): CodeBlock {
            return CodeBlock.of("%M(%S)", CommonNames.resolutionError_unsupportedNullableGeneric, name)
        }
    }

    class FixedCodecSource(declarationName: String?, matcher: CodecTypeMatcher, val member: MemberName) : CodecSource(declarationName, matcher) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            return CodeBlock.of("%M", member)
        }

        override fun toString(): String {
            return "FixedCodecSource(property = `${member.canonicalName}`, type = $matcher)"
        }
    }

    class GenericCodecSource(declarationName: String?, matcher: CodecTypeMatcher, val member: MemberName) : CodecSource(declarationName, matcher) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            val typeArgs = type.arguments.map {
                val argType = it.type?.resolve() ?: return codecNotFoundError("<missing type>")
                registry.getCodec(argType)
            }
            val argFormat = type.arguments.joinToString(", ") { "%L" }
            return CodeBlock.of("%M(${argFormat})", member, *typeArgs.toTypedArray())
        }

        override fun toString(): String {
            return "GenericCodecSource(property = `${member.canonicalName}`, type = $matcher)"
        }
    }

    class StaticFieldCodecSource(matcher: CodecTypeMatcher, val fieldName: String) : CodecSource(null, matcher) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            val targetClass = ClassName(type.declaration.packageName.asString(), type.declaration.simpleName.asString())
            return CodeBlock.of("%T.%N", targetClass, fieldName)
        }
    }
}

package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.buildCodeBlock
import com.teamwizardry.librarianlib.scribe.AutoCodec
import com.teamwizardry.librarianlib.scribe.AutoFactory

// eventually to get the codec registrations from dependencies, encode registrations into a marker annotation, then add
// that annotation to a top-level function:
//   val builtin = resolver.getFunctionDeclarationsByName("com.teamwizardry.librarianlib.scribe.inject.scribeMetadata")
class ScribeRegistry(
    private val logger: KSPLogger
) {
    val codecSources = mutableListOf<CodecSource>()
    val files = mutableSetOf<KSFile>()

    fun process(resolver: Resolver) {
        val registerSymbols = resolver.getSymbolsWithAnnotation(AutoCodec.Register::class.qualifiedName!!).toList()
        registerSymbols
            .filterIsInstance<KSPropertyDeclaration>()
            .filter(KSNode::validate)
            .forEach(::registerProperty)

        registerSymbols
            .filterIsInstance<KSFunctionDeclaration>()
            .filter(KSNode::validate)
            .forEach(::registerFunction)

        codecSources.add(CodecSource.StaticFieldCodecSource(resolver.builtIns.anyType, "CODEC"))
        codecSources.sort()

        logger.info("Scribe: Registered ${codecSources.size} codecs/codec sources")
    }

    fun getCodec(type: KSType): CodeBlock {
        return codecSources.find { it.matches(type) }?.genCodec(this, type)
            ?: CodecSource.errorCodec(type.declaration.simpleName.asString())
    }

    private fun registerProperty(property: KSPropertyDeclaration) {
        property.containingFile?.let(files::add)

        logger.info("@AutoCodec.Register: Registering property `${property.qualifiedName?.asString()}`")
        val registerAnnotation = property.annotations.findByType<AutoCodec.Register>() ?: return
        if (property.parentDeclaration != null) {
            logger.error(
                "@AutoCodec.Register must be on a top-level property or function: `${property.qualifiedName?.asString()}`",
                registerAnnotation
            )
            return
        }
        val registerType = registerAnnotation.arguments[0].value as KSType
        val member = MemberName(property.packageName.asString(), property.simpleName.asString())
        codecSources.add(CodecSource.FixedCodecSource(registerType.makeNullable(), member))
    }

    private fun registerFunction(function: KSFunctionDeclaration) {
        function.containingFile?.let(files::add)

        logger.info("@AutoCodec.Register: Registering function `${function.qualifiedName?.asString()}`")

        val registerAnnotation = function.annotations.findByType<AutoCodec.Register>() ?: return
        if (function.parentDeclaration != null) {
            logger.error(
                "@AutoCodec.Register must be on a top-level property or function: `${function.qualifiedName?.asString()}`",
                registerAnnotation
            )
            return
        }
        val registerType = registerAnnotation.arguments[0].value as KSType

        val member = MemberName(function.packageName.asString(), function.simpleName.asString())
        val typeParameters = registerType.declaration.typeParameters
        val methodParameters = function.parameters

        // todo: validate parameters

        codecSources.add(CodecSource.GenericCodecSource(registerType.makeNullable(), member))
    }

    companion object {
    }
}

sealed class CodecSource(val dataType: KSType) : Comparable<CodecSource> {
    abstract fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock

    fun matches(type: KSType): Boolean {
        return dataType.isAssignableFrom(type)
    }

    override fun compareTo(other: CodecSource): Int {
        val assignableFromOther = matches(other.dataType)
        val assignableToOther = other.matches(dataType)
        return when {
            assignableFromOther && assignableToOther -> 0
            assignableFromOther -> 1
            assignableToOther -> -1
            else -> 0
        }
    }

    companion object {
        fun errorCodec(name: String): CodeBlock {
            return CodeBlock.of("null /* ERROR FINDING CODEC FOR `$name` */")
        }
    }

    class FixedCodecSource(dataType: KSType, val member: MemberName) : CodecSource(dataType) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            return CodeBlock.of("%M", member)
        }
    }

    class GenericCodecSource(dataType: KSType, val member: MemberName) : CodecSource(dataType) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            val typeArgs = type.arguments.map {
                val argType = it.type?.resolve() ?: return errorCodec("<missing type>")
                registry.getCodec(argType)
            }
            val argFormat = type.arguments.joinToString(", ") { "%L" }
            return CodeBlock.of("%M(${argFormat})", member, *typeArgs.toTypedArray())
        }
    }

    class StaticFieldCodecSource(dataType: KSType, val fieldName: String) : CodecSource(dataType) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            val targetClass = ClassName(type.declaration.packageName.asString(), type.declaration.simpleName.asString())
            return CodeBlock.of("%T.%N", targetClass, fieldName)
        }
    }
}
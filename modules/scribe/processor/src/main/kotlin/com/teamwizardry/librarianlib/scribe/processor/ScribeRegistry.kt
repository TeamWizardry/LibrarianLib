package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
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

        codecSources.add(CodecSource.StaticFieldCodecSource(resolver.builtIns.anyType, "CODEC"))
        codecSources.sort()

        logger.info("Scribe: Registered ${codecSources.size} codecs/codec sources")
    }

    fun getCodec(type: KSType): CodeBlock? {
        return codecSources.find { it.matches(type) }?.genCodec(this, type)
    }

    private fun registerProperty(property: KSPropertyDeclaration) {
        property.containingFile?.let(files::add)

        logger.info("@AutoCodec.Register: Registering property `${property.qualifiedName?.asString()}`")
        val registerAnnotations = property.annotations.filterByType<AutoCodec.Register>()
        if (property.parentDeclaration != null) {
            registerAnnotations.forEach {
                logger.error(
                    "@AutoCodec.Register must be on a top-level property: `${property.qualifiedName?.asString()}`",
                    it
                )
            }
            return
        }
        val member = MemberName(property.packageName.asString(), property.simpleName.asString())
        registerAnnotations
            .mapNotNull { it.arguments.getOrNull(0)?.value as? KSType }
            .forEach {
                codecSources.add(CodecSource.FixedCodecSource(it.makeNullable(), member))
            }
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

    class FixedCodecSource(dataType: KSType, val member: MemberName) : CodecSource(dataType) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            return CodeBlock.of("%M", member)
        }
    }

    class StaticFieldCodecSource(dataType: KSType, val fieldName: String) : CodecSource(dataType) {
        override fun genCodec(registry: ScribeRegistry, type: KSType): CodeBlock {
            val targetClass = ClassName(type.declaration.packageName.asString(), type.declaration.simpleName.asString())
            return CodeBlock.of("%T.%N", targetClass, fieldName)
        }
    }
}
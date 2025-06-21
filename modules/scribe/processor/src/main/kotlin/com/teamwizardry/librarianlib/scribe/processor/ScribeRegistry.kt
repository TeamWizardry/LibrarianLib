package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.getPropertyDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.teamwizardry.librarianlib.scribe.AutoCodec
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.sequences.map

class ScribeRegistry(
    private val logger: KSPLogger
) {
    var localCodecSources = listOf<CodecSource>()
    var externalCodecSources = listOf<CodecSource>()
    lateinit var fallbackCodecSource: CodecSource

    fun process(resolver: Resolver, externalMetadata: List<JsonElement>) {

        localCodecSources = scanLocal(resolver)
            .map {
                logger.info("Scribe: Registering local codec source: $it")
                it
            }
            .toList()
            .sorted()
        logger.info("Scribe: Registered ${localCodecSources.size} local codec sources")

        externalCodecSources = scanExternal(resolver, externalMetadata)
            .map {
                logger.info("Scribe: Loading external codec source: $it")
                it
            }
            .toList()
            .sorted()

        logger.info("Scribe: Registered ${externalCodecSources.size} external codec sources")

        fallbackCodecSource = CodecSource.StaticFieldCodecSource(
            null,
            CodecTypeMatcher.create(resolver.builtIns.anyType),
            "CODEC"
        )
    }

    fun saveMetadata(): JsonElement = JsonArray(
        localCodecSources.mapNotNull { it.declarationName }.map(::JsonPrimitive)
    )

    fun getCodec(type: KSType): CodeBlock {
        return findCodecSource(type)?.genCodec(this, type)
            ?: CodecSource.errorCodec(type.declaration.simpleName.asString())
    }

    private fun findCodecSource(type: KSType): CodecSource? {
        return localCodecSources.find { it.matches(type) }
            ?: externalCodecSources.find { it.matches(type) }
            ?: fallbackCodecSource.takeIf { it.matches(type) }
    }

    private fun scanLocal(resolver: Resolver): Sequence<CodecSource> =
        resolver.getSymbolsWithAnnotation(AutoCodec.Register::class.qualifiedName!!)
            .filterIsInstance<KSDeclaration>()
            .filter(KSNode::validate)
            .mapNotNull(::processRegister)

    private fun scanExternal(resolver: Resolver, metadata: List<JsonElement>): Sequence<CodecSource> {
        return metadata.flatMap { it as JsonArray }.asSequence()
            .map { it.jsonPrimitive.content }
            .flatMap {
                resolver.getFunctionDeclarationsByName(it, true) +
                        resolver.getPropertyDeclarationByName(it, true)
            }
            .filterNotNull()
            .filter(KSNode::validate)
            .mapNotNull(::processRegister)
    }

    private fun processRegister(declaration: KSDeclaration): CodecSource? {
        val qualifiedName = declaration.qualifiedName?.asString()
        if (qualifiedName == null) {
            logger.error(
                "@AutoCodec.Register declaration has no qualified name: " +
                        "`${declaration.packageName.asString()}` `${declaration.simpleName.asString()}`",
                declaration
            )
            return null
        }
        val registerAnnotation = declaration.annotations.findByType<AutoCodec.Register>() ?: return null

        if (declaration.parentDeclaration != null) {
            logger.error(
                "@AutoCodec.Register must be on a top-level property or function: `$qualifiedName`",
                registerAnnotation
            )
            return null
        }

        val registerType = (registerAnnotation.arguments[0].value as KSType).makeNullable()
        val matcher = CodecTypeMatcher.create(registerType)
        val member = MemberName(declaration.packageName.asString(), declaration.simpleName.asString())

        return when (declaration) {
            is KSPropertyDeclaration -> {
                CodecSource.FixedCodecSource(qualifiedName, matcher, member)
            }

            is KSFunctionDeclaration -> {
//                val typeParameters = registerType.declaration.typeParameters
//                val methodParameters = declaration.parameters

                // todo: validate parameters

                CodecSource.GenericCodecSource(qualifiedName, matcher, member)
            }

            else -> null
        }


    }

}

package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.writeTo
import com.teamwizardry.librarianlib.scribe.ScribeMetadata
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class ScribeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    var round = 0
    val registry = ScribeRegistry(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val externalMetadata = scanExternalMetadata(resolver)

        registry.process(resolver, externalMetadata.map { it.getValue("registry") })

        val records = RecordCodecGenerator.scan(resolver, logger)
        for (record in records) {
            record.generateCodecsFile(registry).writeTo(codeGenerator, Dependencies.ALL_FILES)
        }

        if (registry.localCodecSources.isNotEmpty()) {
            generateMetadataFile(buildJsonObject {
                put("registry", registry.saveMetadata())
            }).writeTo(codeGenerator, Dependencies.ALL_FILES)
        }

        return emptyList()
    }

    fun scanExternalMetadata(resolver: Resolver): List<JsonObject> {
        val metadataHolders = resolver.getFunctionDeclarationsByName(CommonNames.metadataHolderName.canonicalName, true)
            .filter { it.containingFile == null }
            .toList()
        logger.info("Scribe: Found ${metadataHolders.size} metadata holders")

        return metadataHolders
            .mapNotNull { it.annotations.findByType<ScribeMetadata>() }
            .map { it.arguments[0].value as String }
            .map { Json.parseToJsonElement(it).jsonObject }
            .toList()
    }

    fun generateMetadataFile(metadata: JsonObject): FileSpec {
        return FileSpec.builder(CommonNames.metadataHolderName.packageName, "ScribeMetadata")
            .indent("    ")
            .addFunction(
                FunSpec.builder(CommonNames.metadataHolderName)
                    .addModifiers(KModifier.PRIVATE)
                    .addAnnotation(
                        AnnotationSpec.builder(ScribeMetadata::class)
                            .addMember("%S", metadata.toString())
                            .build()
                    )
                    .build()
            )
            .build()
    }
}
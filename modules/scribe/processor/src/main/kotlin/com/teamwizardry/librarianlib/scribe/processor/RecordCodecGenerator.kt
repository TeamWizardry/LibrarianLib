package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.withIndent
import com.teamwizardry.librarianlib.scribe.AutoCodec

class RecordCodecGenerator(
    val sourceFile: KSFile,
    val recordClassName: ClassName,
    val packageName: String,
    val objectName: String,
    val fields: List<RecordField>
) {
    companion object {
        fun scan(
            resolver: Resolver,
            logger: KSPLogger
        ): List<RecordCodecGenerator> {
            return resolver.getSymbolsWithAnnotation(AutoCodec.Record::class.qualifiedName.orEmpty())
                .filterIsInstance<KSClassDeclaration>()
                .mapNotNull { build(it, logger) }
                .toList()
        }

        private fun build(classDeclaration: KSClassDeclaration, logger: KSPLogger): RecordCodecGenerator? {
            val classQualifiedName = classDeclaration.qualifiedName?.asString()
            val classPackageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()

            logger.info("Scribe: Generating codec for type `$classQualifiedName`")

            val recordAnnotation = classDeclaration.annotations.findByType<AutoCodec.Record>() ?: return null
            val constructor = classDeclaration.primaryConstructor ?: run {
                logger.error(
                    "@AutoCodec.Record: No primary constructor for `$classQualifiedName`",
                    recordAnnotation
                )
                return null
            }

            if (constructor.parameters.isEmpty()) {
                logger.error(
                    "@AutoCodec.Record: No constructor parameters for `$classQualifiedName`",
                    constructor
                )
                return null
            }
//            val companion = classDeclaration.declarations.filterIsInstance<KSClassDeclaration>().find { it.isCompanionObject }


            val fields = constructor.parameters.map { param ->
                val key: String
                val paramName = param.name?.asString().orEmpty()
                param.hasDefault
                val paramType = param.type.resolve()

                val fieldAnnotation = param.annotations.findByType<AutoCodec.Field>()
                if (fieldAnnotation == null) {
                    logger.error(
                        "@AutoCodec.Record: No `@AutoCodec.Field` annotation for constructor parameter `$paramName` in `$classQualifiedName`",
                        param
                    )
                    key = paramName
                } else {
                    key = fieldAnnotation.arguments[0].value as String
                }

                RecordField(
                    key = key,
                    type = paramType,
                    fieldName = paramName,
                    nullable = paramType.nullability != Nullability.NOT_NULL,
                    paramName = paramName,
                    hasDefault = param.hasDefault,
                )
            }

            // todo: error for conflicting field keys
            // todo: warning when class doesn't have `CODEC` field

            return RecordCodecGenerator(
                classDeclaration.containingFile!!,
                classDeclaration.toClassName(),
                classPackageName,
                className + "Codecs",
                fields,
            )
        }
    }

    data class RecordField(
        val key: String,
        val type: KSType,
        val fieldName: String,
        val nullable: Boolean,
        val paramName: String,
        val hasDefault: Boolean,
    )


    fun generateCodecsFile(registry: ScribeRegistry): FileSpec {
        //todo: allow recursion
        return FileSpec.builder(packageName, objectName)
            .indent("    ")
            .addType(
                TypeSpec.objectBuilder(objectName).apply {
                    addProperty(
                        PropertySpec.builder(
                            name = "MAP_CODEC",
                            type = CommonNames.MapCodec.parameterizedBy(recordClassName)
                        )
                            .addAnnotation(JvmStatic::class)
                            .initializer(generateNBTCodec(registry))
                            .build()
                    )

                    addProperty(
                        PropertySpec.builder(
                            name = "CODEC",
                            type = CommonNames.Codec.parameterizedBy(recordClassName)
                        )
                            .addAnnotation(JvmStatic::class)
                            .initializer("MAP_CODEC.codec()")
                            .build()
                    )
                }.build()
            )
            .build()
    }

    private fun generateNBTCodec(registry: ScribeRegistry): CodeBlock {
        return buildCodeBlock {
            addStatement("%M(", CommonNames.ScribeRecordCodec.member("recordCodec"))
            withIndent {
                addStatement("::%T,", recordClassName)
                addStatement("listOf(")
                withIndent {
                    for (field in fields) {
                        add("«%T::%N.%M(%S, %L",
                            recordClassName,
                            field.fieldName,
                            CommonNames.ScribeRecordCodec.member("recordField"),
                            field.key,
                            registry.getCodec(field.type.makeNotNullable()),
                        )
                        if (field.nullable) add(", nullable = true")
                        if (field.hasDefault) add(", hasDefault = true")
                        if (field.paramName != field.fieldName) add(", paramName = %S", field.paramName)
                        add("),\n»")
                    }
                }
                addStatement(")")
            }
            addStatement(")")
        }
    }
}
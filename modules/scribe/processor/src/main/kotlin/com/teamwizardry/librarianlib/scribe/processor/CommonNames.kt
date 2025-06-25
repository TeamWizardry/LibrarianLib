package com.teamwizardry.librarianlib.scribe.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

object CommonNames {
    val Codec = className("com.mojang.serialization.Codec")
    val MapCodec = className("com.mojang.serialization.MapCodec")
    val DataResult = className("com.mojang.serialization.DataResult")
    val ScribeRecordCodec = className("com.teamwizardry.librarianlib.scribe.helpers.ScribeRecordCodec")

    val resolutionError_codecNotFound = MemberName("com.teamwizardry.librarianlib.scribe.helpers", "codecNotFound")
    val resolutionError_unsupportedNullableGeneric = MemberName("com.teamwizardry.librarianlib.scribe.helpers", "unsupportedNullableGeneric")
    val dataResult_orAbort = MemberName("com.teamwizardry.librarianlib.scribe.util", "orAbort")
    val metadataHolderName = MemberName("com.teamwizardry.librarianlib.scribe.metadata", "scribeMetadataHolder")

    private fun className(qualifiedName: String): ClassName {
        return ClassName(
            qualifiedName.substringBeforeLast('.'),
            qualifiedName.substringAfterLast('.')
        )
    }
}

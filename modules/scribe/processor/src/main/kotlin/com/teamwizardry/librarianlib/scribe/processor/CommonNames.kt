package com.teamwizardry.librarianlib.scribe.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

object CommonNames {
    val Codec = className("com.mojang.serialization.Codec")
    val MapCodec = className("com.mojang.serialization.MapCodec")
    val DataResult = className("com.mojang.serialization.DataResult")
    val ScribeRecordCodec = className("com.teamwizardry.librarianlib.scribe.helpers.ScribeRecordCodec")

    private fun className(qualifiedName: String): ClassName {
        return ClassName(
            qualifiedName.substringBeforeLast('.'),
            qualifiedName.substringAfterLast('.')
        )
    }
}

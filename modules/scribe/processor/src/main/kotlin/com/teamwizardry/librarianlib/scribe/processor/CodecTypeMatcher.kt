package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.symbol.KSType

class CodecTypeMatcher private constructor(private val matchType: KSType) : Comparable<CodecTypeMatcher> {

    fun matches(type: KSType): Boolean {
        return matchType.isAssignableFrom(type)
    }

    override fun compareTo(other: CodecTypeMatcher): Int {
        val assignableFromOther = matches(other.matchType)
        val assignableToOther = other.matches(matchType)
        return when {
            assignableFromOther && assignableToOther -> 0
            assignableFromOther -> 1
            assignableToOther -> -1
            else -> 0
        }
    }

    override fun toString(): String {
        val qualifiedName = matchType.declaration.qualifiedName
        return if (qualifiedName == null) {
            "<unknown name>"
        } else {
            "`${qualifiedName.asString()}`"
        }
    }

    companion object {
        fun create(type: KSType): CodecTypeMatcher {
            return CodecTypeMatcher(type.makeNullable())
        }
    }
}
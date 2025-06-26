package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.symbol.KSType

sealed class CodecTypeMatcher(protected val precedence: Int) : Comparable<CodecTypeMatcher> {
    abstract fun matches(type: KSType): Boolean

    class SimpleTypeMatcher(private val matchType: KSType) : CodecTypeMatcher(0) {
        override fun matches(type: KSType): Boolean {
            return matchType.isAssignableFrom(type)
        }

        override fun compareTo(other: CodecTypeMatcher): Int {
            if (other !is SimpleTypeMatcher)
                return precedence - other.precedence

            return compareTypes(matchType, other.matchType)
        }

        override fun toString(): String {
            val qualifiedName = matchType.declaration.qualifiedName
            return if (qualifiedName == null) {
                "<unknown name>"
            } else {
                "`${qualifiedName.asString()}`"
            }
        }
    }

    class ArrayTypeMatcher(private val elementType: KSType) : CodecTypeMatcher(-1) {
        override fun matches(type: KSType): Boolean {
            if(type.declaration.qualifiedName?.asString() != "kotlin.Array") return false
            val typeArg = type.arguments[0].type?.resolve() ?: return false
            return elementType.isAssignableFrom(typeArg)
        }

        override fun compareTo(other: CodecTypeMatcher): Int {
            if (other !is ArrayTypeMatcher)
                return precedence - other.precedence

            return compareTypes(elementType, other.elementType)
        }

        override fun toString(): String {
            val qualifiedName = elementType.declaration.qualifiedName
            return if (qualifiedName == null) {
                "Array<unknown name>"
            } else {
                "`Array<${qualifiedName.asString()}>`"
            }
        }
    }

    object FallbackTypeMatcher : CodecTypeMatcher(999) {
        override fun matches(type: KSType): Boolean {
            return true
        }

        override fun compareTo(other: CodecTypeMatcher): Int {
            return precedence - other.precedence
        }
    }

    companion object {
        fun create(type: KSType): CodecTypeMatcher {
            return CodecTypeMatcher.SimpleTypeMatcher(type.makeNotNullable())
        }
    }
}

private fun compareTypes(self: KSType, other: KSType): Int {
    val assignableFromOther = self.isAssignableFrom(other)
    val assignableToOther = other.isAssignableFrom(self)
    return when {
        assignableFromOther && assignableToOther -> 0
        assignableFromOther -> 1
        assignableToOther -> -1
        else -> 0
    }
}
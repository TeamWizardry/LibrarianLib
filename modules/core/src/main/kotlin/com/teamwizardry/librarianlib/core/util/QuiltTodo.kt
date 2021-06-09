@file:Suppress("FunctionName")

package com.teamwizardry.librarianlib.core.util

import org.intellij.lang.annotations.Language
import java.lang.UnsupportedOperationException

// Marker functions for code that has yet to be ported. Likely to be mostly used for forge patches that need to be
// replaced

public fun Any?.QUILT_TODO(@Language("kotlin") code: String, comment: String) {
    throw UnsupportedOperationException("Quilt todo: $code // $comment")
}

public fun <T> Any?.QUILT_TODO(@Language("kotlin") code: String, comment: String): T {
    throw UnsupportedOperationException("Quilt todo: $code // $comment")
}

public fun Any?.QUILT_TODO(comment: String) {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

public fun <T> Any?.QUILT_TODO(comment: String): T {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

public fun Any?.QUILT_TODO(comment: String, code: () -> Unit) {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

public fun <T> Any?.QUILT_TODO(comment: String, code: () -> Unit): T {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

@file:Suppress("FunctionName")

package com.teamwizardry.librarianlib.core.util

import java.lang.UnsupportedOperationException

// Marker functions for code that has yet to be ported. Likely to be mostly used for forge patches that need to be
// replaced

public fun <T> Any?.QUILT_TODO(code: String, comment: String): T {
    throw UnsupportedOperationException("Quilt todo: $code // $comment")
}

public fun <T> Any?.QUILT_TODO(comment: String): T {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

public fun <T> Any?.QUILT_TODO(comment: String, code: () -> Unit): T {
    throw UnsupportedOperationException("Quilt todo: $comment")
}

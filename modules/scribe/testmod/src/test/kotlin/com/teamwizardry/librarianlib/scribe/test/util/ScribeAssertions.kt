package com.teamwizardry.librarianlib.scribe.test.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.opentest4j.AssertionFailedError

fun <T> assertJsonCodec(codec: Codec<T>, value: T, @Language("json") expectedJson: String, message: String? = null) {
    val parsedExpected = JsonParser.parseString(expectedJson)
    val messagePrefix = if (message == null) "" else "$message > "
    val encodeResult = assertSuccess(codec.encodeJson(value), "${messagePrefix}JSON encode")
    if (parsedExpected != encodeResult) {
        throwFailure(
            message = "${messagePrefix}Encoded JSON",
            expected = parsedExpected.prettyPrint(),
            actual = encodeResult.prettyPrint()
        )
    }

    val decodeResult = assertSuccess(codec.decodeJson(encodeResult), "${messagePrefix}JSON decode")
    assertEquals(value, decodeResult, "${messagePrefix}Decoded value")
}

/**
 * Asserts the DataResult is successful, and returns its value
 */
fun <T> assertSuccess(dataResult: DataResult<T>, message: String? = null): T {
    when (dataResult) {
        is DataResult.Success -> return dataResult.value
        is DataResult.Error -> throwFailure(
            message = message,
            expected = "DataResult.Success",
            actual = dataResult.message()
        )
    }
}

fun assertContains(expectedSubstring: String, actualText: String, message: String? = null) {
    if (expectedSubstring !in actualText) {
        throwFailure(
            message = message,
            reason = "Missing substring `$expectedSubstring`",
            expected = "",
            actual = actualText
        )
    }
}

fun throwFailure(
    message: String? = null,
    reason: String? = null,
    expected: Any? = NO_VALUE,
    actual: Any? = NO_VALUE,
    cause: Throwable? = null
): Nothing {
    val builder = AssertionFailureBuilder.assertionFailure().message(message).reason(reason).cause(cause)
    if (expected != NO_VALUE) builder.expected(expected)
    if (actual != NO_VALUE) builder.actual(actual)
    throw builder.build()
}

fun throwFailure(
    message: (() -> String?)?,
    reason: String? = null,
    expected: Any? = NO_VALUE,
    actual: Any? = NO_VALUE,
    cause: Throwable? = null
): Nothing {
    val builder = AssertionFailureBuilder.assertionFailure().message(message).reason(reason).cause(cause)
    if (expected != NO_VALUE) builder.expected(expected)
    if (actual != NO_VALUE) builder.actual(actual)
    throw builder.build()
}

private val NO_VALUE = Any() // unique object to identify absent expected/actual
package com.teamwizardry.librarianlib.scribe.test.util

import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertAll
import kotlin.contracts.contract

fun <T> assertJsonEncode(codec: Codec<T>, value: T, @Language("json") json: String, message: String? = null) {
    val expectedJson = JsonParser.parseString(json)
    val messagePrefix = if (message == null) "" else "$message > "
    val encodeResult = assertSuccess(codec.encodeJson(value), "${messagePrefix}JSON encode")
    if (expectedJson != encodeResult) {
        throwFailure(
            message = "${messagePrefix}Encoded JSON",
            expected = expectedJson.prettyPrint(),
            actual = encodeResult.prettyPrint()
        )
    }
}

fun <T> assertJsonEncodeError(codec: Codec<T>, value: T, expectedError: String, assertMessage: String? = null) {
    assertError(codec.encodeJson(value), expectedError, assertMessage)
}

fun <T> assertJsonDecode(codec: Codec<T>, value: T, @Language("json") json: String, message: String? = null) {
    val parsedJson = JsonParser.parseString(json)
    val messagePrefix = if (message == null) "" else "$message > "
    val decodeResult = assertSuccess(codec.decodeJson(parsedJson), "${messagePrefix}JSON decode")
    assertEquals(value, decodeResult, "${messagePrefix}Decoded value")
}

fun <T> assertJsonDecodeError(codec: Codec<T>, @Language("json") json: String, expectedError: String, assertMessage: String? = null) {
    assertError(codec.decodeJson(json), expectedError, assertMessage)
}

fun <T> assertJsonEncodeDecode(codec: Codec<T>, value: T, @Language("json") json: String, message: String? = null) {
    assertAll(
        { assertJsonEncode(codec, value, json, message) },
        { assertJsonDecode(codec, value, json, message) }
    )
}

/**
 * Asserts the DataResult is successful, and returns its value
 */
fun <T> assertSuccess(dataResult: DataResult<T>, message: String? = null): T {
    contract {
        returns() implies (dataResult is DataResult.Success)
    }
    when (dataResult) {
        is DataResult.Success -> return dataResult.value
        is DataResult.Error -> throwFailure(
            message = message,
            expected = "DataResult.Success",
            actual = dataResult
        )
    }
}

/**
 * Asserts the DataResult is successful, and returns its value
 */
fun <T> assertError(dataResult: DataResult<T>, expectedMessage: String, assertMessage: String? = null) {
    contract {
        returns() implies (dataResult is DataResult.Error)
    }
    when (dataResult) {
        is DataResult.Success -> throwFailure(
            message = assertMessage,
            expected = "DataResult.Error['$expectedMessage']",
            actual = dataResult
        )
        is DataResult.Error -> {
            if (dataResult.message() != expectedMessage) {
                throwFailure(
                    message = assertMessage,
                    expected = "DataResult.Error['$expectedMessage']",
                    actual = dataResult.toString()
                )
            }
        }
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
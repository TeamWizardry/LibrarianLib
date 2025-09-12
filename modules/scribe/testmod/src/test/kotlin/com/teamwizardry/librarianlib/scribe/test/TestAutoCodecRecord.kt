package com.teamwizardry.librarianlib.scribe.test

import com.teamwizardry.librarianlib.scribe.test.util.ScribeTestHelper
import com.teamwizardry.librarianlib.scribe.test.util.assertJsonDecode
import com.teamwizardry.librarianlib.scribe.test.util.assertJsonDecodeError
import com.teamwizardry.librarianlib.scribe.test.util.assertJsonEncode
import com.teamwizardry.librarianlib.scribe.test.util.assertJsonEncodeDecode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class TestAutoCodecRecord {

    @Test
    fun `simple nested class`() {
        val testHelper = ScribeTestHelper()
        testHelper.kotlin(
            "SimpleTest",
            """
            class Outer {
                @AutoCodec.Record
                data class SimpleTest(@AutoCodec.Field("Value") val value: String) {
                    companion object {
                        val CODEC = SimpleTestCodecs.CODEC // doesn't include `Outer`
                    }
                }
            }
            """.trimIndent()
        )

        val result = testHelper.compile().assertSuccess()
        val testClass = result.getRecordClass("Outer\$SimpleTest")

        assertAll(
            { assertJsonEncode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonDecode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            {
                assertJsonDecodeError(
                    testClass.codec,
                    """{}""",
                    "Errors decoding Outer.SimpleTest from MapLike[{}]: Value => [ Key missing ]",
                    "Missing key"
                )
            },
        )
    }

    @Test
    fun `simple record with a single key`() {
        val testHelper = ScribeTestHelper()
        testHelper.kotlin(
            "SimpleTest",
            """
            @AutoCodec.Record
            data class SimpleTest(@AutoCodec.Field("Value") val value: String) {
                companion object {
                    val CODEC = SimpleTestCodecs.CODEC
                }
            }
            """.trimIndent()
        )

        val result = testHelper.compile().assertSuccess()
        val testClass = result.getRecordClass("SimpleTest")

        assertAll(
            { assertJsonEncode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonDecode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonDecode(testClass.codec, testClass("wow"), """{ "Value": "wow", "Extra": 0 }""") },
            {
                assertJsonDecodeError(
                    testClass.codec,
                    """{}""",
                    "Errors decoding SimpleTest from MapLike[{}]: Value => [ Key missing ]",
                    "Missing key"
                )
            },
            {
                assertJsonDecodeError(
                    testClass.codec,
                    """{ "Value": 5 }""",
                    "Errors decoding SimpleTest from MapLike[{\"Value\":5}]: Value => [ Not a string: 5 ]",
                    "Wrong value type"
                )
            },
        )
    }

    @Test
    fun `simple record with a nullable key`() {
        val testHelper = ScribeTestHelper()
        testHelper.kotlin(
            "SimpleTest",
            """
            @AutoCodec.Record
            data class SimpleTest(@AutoCodec.Field("Value") val value: String?) {
                companion object {
                    val CODEC = SimpleTestCodecs.CODEC
                }
            }
            """.trimIndent()
        )

        val result = testHelper.compile().assertSuccess()
        val testClass = result.getRecordClass("SimpleTest")
        assertAll(
            { assertJsonEncodeDecode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonEncodeDecode(testClass.codec, testClass(null), """{}""") },
        )
    }

    @Test
    fun `simple record with a default value`() {
        val testHelper = ScribeTestHelper()
        testHelper.kotlin(
            "SimpleTest",
            """
            @AutoCodec.Record
            data class SimpleTest(@AutoCodec.Field("Value") val value: String = "default") {
                companion object {
                    val CODEC = SimpleTestCodecs.CODEC
                }
            }
            """.trimIndent()
        )

        val result = testHelper.compile().assertSuccess()
        val testClass = result.getRecordClass("SimpleTest")

        assertAll(
            { assertJsonEncode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonDecode(testClass.codec, testClass("wow"), """{ "Value": "wow" }""") },
            { assertJsonEncode(testClass.codec, testClass("default"), """{ "Value": "default" }""") },
            { assertJsonDecode(testClass.codec, testClass("default"), """{}""") },
            {
                assertJsonDecodeError(
                    testClass.codec,
                    """{ "Value": 5 }""",
                    "Errors decoding SimpleTest from MapLike[{\"Value\":5}]: Value => [ Not a string: 5 ]",
                    "Wrong value type"
                )
            },
        )
    }

    @Test
    fun `scribe record error messages`() {
        val testHelper = ScribeTestHelper()
        testHelper.kotlin(
            "SimpleTest",
            """
            @AutoCodec.Record
            data class SimpleTest(
                @AutoCodec.Field("StringValue") val stringValue: String,
                @AutoCodec.Field("IntValue") val intValue: Int,
                @AutoCodec.Field("OptionalValue") val optionalValue: String?,
            ) {
                companion object {
                    val CODEC = SimpleTestCodecs.CODEC
                }
            }
            """.trimIndent()
        )

        val result = testHelper.compile().assertSuccess()
        val testClass = result.getRecordClass("SimpleTest")

        assertAll(
            {
                assertJsonDecode(
                    testClass.codec,
                    testClass("x", 1, "y"),
                    """{ "StringValue": "x", "IntValue": 1, "OptionalValue": "y" }"""
                )
            },
            {
                // vanilla: `No key IntValue in MapLike[{"StringValue":"x","OptionalValue":"y"}]`
                assertJsonDecodeError(
                    testClass.codec,
                    """{ "StringValue": "x", "OptionalValue": "y" }""",
                    """Errors decoding SimpleTest from MapLike[{"StringValue":"x","OptionalValue":"y"}]: IntValue => [ Key missing ]""",
                    "Single missing key"
                )
            },
            {
                // vanilla: `Not a string: 5`
                assertJsonDecodeError(
                    testClass.codec,
                    """{ "StringValue": 5, "IntValue": 1, "OptionalValue": "y" }""",
                    """Errors decoding SimpleTest from MapLike[{"StringValue":5,"IntValue":1,"OptionalValue":"y"}]: StringValue => [ Not a string: 5 ]""",
                    "Single wrong value type"
                )
            },
            {
                // vanilla: `No key IntValue in MapLike[{"StringValue":5,"OptionalValue":"y"}]; Not a string: 5`
                assertJsonDecodeError(
                    testClass.codec,
                    """{ "StringValue": 5, "OptionalValue": "y" }""",
                    """Errors decoding SimpleTest from MapLike[{"StringValue":5,"OptionalValue":"y"}]: StringValue => [ Not a string: 5 ], IntValue => [ Key missing ]""",
                    "Wrong value type and missing value"
                )
            },
        )
    }
}
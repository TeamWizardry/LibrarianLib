package com.teamwizardry.librarianlib.scribe.test

import com.mojang.serialization.Codec
import com.teamwizardry.librarianlib.scribe.test.util.ScribeTestHelper
import com.teamwizardry.librarianlib.scribe.test.util._createSimpleInstance
import com.teamwizardry.librarianlib.scribe.test.util._getStaticFieldValue
import com.teamwizardry.librarianlib.scribe.test.util.assertJsonCodec
import org.junit.jupiter.api.Test

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

        val testClass = result.getClass("Outer\$SimpleTest")

        assertJsonCodec(
            testClass._getStaticFieldValue<Codec<Any>>("CODEC"),
            testClass._createSimpleInstance("wow"),
            """{ "Value": "wow" }"""
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
        val testClass = result.getClass("SimpleTest")

        assertJsonCodec(
            testClass._getStaticFieldValue<Codec<Any>>("CODEC"),
            testClass._createSimpleInstance("wow"),
            """{ "Value": "wow" }"""
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
        val testClass = result.getClass("SimpleTest")
        val codec = testClass._getStaticFieldValue<Codec<Any>>("CODEC")

        assertJsonCodec(
            codec,
            testClass._createSimpleInstance("wow"),
            """{ "Value": "wow" }"""
        )
        assertJsonCodec(
            codec,
            testClass._createSimpleInstance(null),
            """{}"""
        )
    }
}
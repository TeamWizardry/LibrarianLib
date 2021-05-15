package com.teamwizardry.librarianlib.testcore.test

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

class UnitTestTests {
    @Test
    fun nothingTest() {
    }

    @Test
    fun simpleAssertionTest() {
        assertTrue(true)
    }

    @Test
    fun simpleThrowsTest() {
        assertThrows<NullPointerException> { null!! }
    }

    @Test
    fun simpleFailingTest() {
        assertTrue(false)
    }
}
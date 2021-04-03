package com.teamwizardry.librarianlib.testbase.testmod

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
        assertThrows<KotlinNullPointerException> { null!! }
    }

    @Test
    fun simpleFailingTest() {
        assertTrue(false)
    }
}
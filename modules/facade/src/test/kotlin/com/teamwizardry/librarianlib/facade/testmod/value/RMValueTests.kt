package com.teamwizardry.librarianlib.facade.testmod.value

import com.teamwizardry.librarianlib.facade.value.RMValue
import com.teamwizardry.librarianlib.facade.value.RMValueBoolean
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.facade.value.RMValueInt
import com.teamwizardry.librarianlib.facade.value.RMValueLong
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RMValueTests {
    @Test
    fun `setting the value of an RMValue should call the change listener with the correct parameters`() {
        var called = false
        var old: String? = null
        var new: String? = null
        val rmValue = RMValue("old", null) { _old, _new ->
            called = true
            old = _old
            new = _new
        }
        rmValue.set("new")
        assertTrue(called)
        assertEquals("old", old)
        assertEquals("new", new)
    }

    @Test
    fun `setting the value of an RMValue to an equal value should not call the change listener`() {
        var called = false
        val rmValue = RMValue("old", null) { _, _ ->
            called = true
        }
        rmValue.set("old")
        assertFalse(called)
    }

    @Test
    fun `setting the value of an RMValueBoolean should call the change listener with the correct parameters`() {
        var called = false
        var old: Boolean? = null
        var new: Boolean? = null
        val rmValue = RMValueBoolean(true) { _old, _new ->
            called = true
            old = _old
            new = _new
        }
        rmValue.set(false)
        assertTrue(called)
        assertEquals(true, old)
        assertEquals(false, new)
    }

    @Test
    fun `setting the value of an RMValueBoolean to an equal value should not call the change listener`() {
        var called = false
        val rmValue = RMValueBoolean(true) { _, _ ->
            called = true
        }
        rmValue.set(true)
        assertFalse(called)
    }

    @Test
    fun `setting the value of an RMValueDouble should call the change listener with the correct parameters`() {
        var called = false
        var old: Double? = null
        var new: Double? = null
        val rmValue = RMValueDouble(1.0) { _old, _new ->
            called = true
            old = _old
            new = _new
        }
        rmValue.set(2.0)
        assertTrue(called)
        assertEquals(1.0, old)
        assertEquals(2.0, new)
    }

    @Test
    fun `setting the value of an RMValueDouble to an equal value should not call the change listener`() {
        var called = false
        val rmValue = RMValueDouble(1.0) { _, _ ->
            called = true
        }
        rmValue.set(1.0)
        assertFalse(called)
    }

    @Test
    fun `setting the value of an RMValueInt should call the change listener with the correct parameters`() {
        var called = false
        var old: Int? = null
        var new: Int? = null
        val rmValue = RMValueInt(1) { _old, _new ->
            called = true
            old = _old
            new = _new
        }
        rmValue.set(2)
        assertTrue(called)
        assertEquals(1, old)
        assertEquals(2, new)
    }

    @Test
    fun `setting the value of an RMValueInt to an equal value should not call the change listener`() {
        var called = false
        val rmValue = RMValueInt(1) { _, _ ->
            called = true
        }
        rmValue.set(1)
        assertFalse(called)
    }

    @Test
    fun `setting the value of an RMValueLong should call the change listener with the correct parameters`() {
        var called = false
        var old: Long? = null
        var new: Long? = null
        val rmValue = RMValueLong(1) { _old, _new ->
            called = true
            old = _old
            new = _new
        }
        rmValue.set(2)
        assertTrue(called)
        assertEquals(1, old)
        assertEquals(2, new)
    }

    @Test
    fun `setting the value of an RMValueLong to an equal value should not call the change listener`() {
        var called = false
        val rmValue = RMValueLong(1) { _, _ ->
            called = true
        }
        rmValue.set(1)
        assertFalse(called)
    }
}
package com.teamwizardry.librarianlib.math

import java.lang.IndexOutOfBoundsException

class Matrix3dStack: MutableMatrix3d() {
    private val stack = mutableListOf(MutableMatrix3d())
    /**
     * The number of matrices currently in the stack.
     */
    var depth = 0
        private set

    /**
     * Pushes the current state onto the stack
     */
    fun push() {
        if(depth == stack.size) {
            stack.add(MutableMatrix3d())
        }
        stack[depth].set(this)
        depth++
    }

    /**
     * Pops the last state off the stack
     */
    fun pop() {
        if(depth == 0) {
            throw IndexOutOfBoundsException("Tried to pop off an empty stack")
        }
        depth--
        this.set(stack[depth])
        // keep at most 10 matrices above us in the stack.
        if(stack.size - depth > 10) {
            stack.removeAt(stack.lastIndex)
        }
    }

    /**
     * Asserts that the current stack depth is equal to [expected]. If it isn't, (indicating mismatched pushes/pops
     * somewhere), an [UnevenStackOperationException] is thrown.
     *
     * This function is typically used as so:
     * ```kotlin
     * val depth = stack.depth
     * thing.render(stack, ...)
     * stack.assertDepth(depth)
     * ```
     *
     * @param expected the expected depth
     * @param message a custom exception message, or null. Information about the actual and expected depth will be
     * appended to this message if it is present
     * @throws UnevenStackOperationException if the stack depth is not equal to the expected value
     */
    @JvmOverloads
    fun assertDepth(expected: Int, message: String? = null) {
        if(depth == expected) return
        throw UnevenStackOperationException(if(message == null) {
            "Expected a stack depth of $expected, but the stack depth was $depth."
        } else {
            "$message (expected $expected, actually $depth)"
        })
    }

    /**
     * Asserts that the passed block performs an equal number of pushes and pops. If it doesn't, an
     * [UnevenStackOperationException] is thrown.
     *
     * This function is typically used as so:
     * ```kotlin
     * stack.assertEvenDepth {
     *     thing.render(stack, ...)
     * }
     * ```
     *
     * @param message a custom exception message, or null. Information about the actual and expected depth will be
     * appended to this message if it is present
     * @throws UnevenStackOperationException if the stack depth is not equal to the expected value
     */
    inline fun <R> assertEvenDepth(message: String? = null, block: () -> R): R {
        val expected = this.depth
        val returnValue = block()
        assertDepth(expected)
        return returnValue
    }
}
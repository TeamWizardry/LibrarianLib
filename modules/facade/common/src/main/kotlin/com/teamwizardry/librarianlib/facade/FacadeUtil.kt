@file:JvmName("FacadeUtil")
package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import java.util.function.Supplier

/**
 * Run the passed block, opening a safety net screen if there's an uncaught exception.
 *
 * @param action The action that is being performed in the passed block. Should fit in the sentence "Safety net caught
 * an exception while _____". For example "rendering", "handling mouse input", "updating layout"
 */
public fun safetyNet(action: String, block: Runnable) {
    try {
        block.run()
    } catch (e: Exception) {
        Client.openScreen(SafetyNetErrorScreen(action, e))
    }
}

/**
 * Run the passed block, returning its value or opening a safety net screen if there's an uncaught exception.
 *
 * @param action The action that is being performed in the passed block. Should fit in the sentence "Safety net caught
 * an exception while _____". For example "rendering", "handling mouse input", "updating layout"
 * @return The result of the passed block or the result of [defaultValue] if an exception occurred.
 */
public fun <T> safetyNet(action: String, defaultValue: Supplier<T>, block: Supplier<T>): T? {
    try {
        return block.get()
    } catch (e: Exception) {
        Client.openScreen(SafetyNetErrorScreen(action, e))
        return defaultValue.get()
    }
}

/**
 * Run the passed block, opening a safety net screen if there's an uncaught exception.
 *
 * @param action The action that is being performed in the passed block. Should fit in the sentence "Safety net caught
 * an exception while _____". For example "rendering", "handling mouse input", "updating layout"
 */
@JvmSynthetic
public inline fun safetyNet(action: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Client.openScreen(SafetyNetErrorScreen(action, e))
    }
}

/**
 * Run the passed block, returning its value or opening a safety net screen if there's an uncaught exception.
 *
 * @param action The action that is being performed in the passed block. Should fit in the sentence "Safety net caught
 * an exception while _____". For example "rendering", "handling mouse input", "updating layout"
 * @return The result of the passed block or null if an exception occurred.
 */
@JvmSynthetic
public inline fun <T> safetyNet(action: String, defaultValue: () -> T, block: () -> T): T {
    try {
        return block()
    } catch (e: Exception) {
        Client.openScreen(SafetyNetErrorScreen(action, e))
        return defaultValue()
    }
}

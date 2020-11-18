package com.teamwizardry.librarianlib.foundation.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Similar to [LogManager] but it makes pretty names.
 */
public open class ModLogManager(
    /**
     * The base name of all loggers. Loggers (aside from the default `makeLogger(null)` logger) will be named
     * `<baseName> (<label>)`.
     *
     * Changing this value won't affect existing loggers.
     */
    public var baseName: String
) {
    private val modLoggers = mutableMapOf<String?, Logger>()

    /**
     * Create a logger for the specified class
     */
    public fun makeLogger(clazz: Class<*>): Logger {
        return makeLogger(clazz.simpleName)
    }

    /**
     * Create a logger for the specified class
     */
    public inline fun <reified T> makeLogger(): Logger {
        return makeLogger(T::class.java)
    }

    /**
     * Create a logger with the specified label.
     */
    public fun makeLogger(label: String?): Logger {
        return modLoggers.getOrPut(label) {
            LogManager.getLogger(label?.let { "$baseName ($it)" } ?: baseName)
        }
    }
}
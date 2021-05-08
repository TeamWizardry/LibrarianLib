package com.teamwizardry.librarianlib.core.util

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.config.Configurator

/**
 * A class that creates nicely named mod loggers, and allows enabling debugging on a per-mod basis.
 *
 * The default (null) logger will be named `<humanName>`, and all others will either be named
 * `<humanName>|<custom name>` or `<humanName>|<classname>`, depending on which method was used to create it.
 *
 * Mods can have their loggers enabled by passing their comma-separated [mod IDs][modid] in the
 * `librarianlib.logging.debug` system property. For example, by adding `-Dlibrarianlib.logging.debug=someid,anotherid`
 * in the VM parameters. The elements can also use primitive glob expressions (e.g. `so*` would enable both `something`
 * and `socool`)
 */
public class ModLogManager(private val modid: String, private val humanName: String) {
    /**
     * Whether debugging is enabled for this module.
     */
    public var debugEnabled: Boolean = debugPatterns.any { it.matches(modid) }
        set(value) {
            if (field != value) {
                registeredLoggers.forEach { (logger, level) ->
                    Configurator.setLevel(logger.name, if (value) Level.DEBUG else level)
                }
            }
            field = value
        }

    /**
     * Loggers to update based on the debug flag.
     */
    private val registeredLoggers = mutableListOf<TrackedLogger>()
    private val modLoggers = mutableMapOf<String?, Logger>()

    public val root: Logger = makeLogger(null)

    /**
     * Create a logger for the given class.
     */
    public fun makeLogger(clazz: Class<*>): Logger {
        return makeLogger(clazz.canonicalName.removePrefix(clazz.`package`.name + "."))
    }

    /**
     * Create a logger for the specified class.
     */
    public inline fun <reified T> makeLogger(): Logger {
        return makeLogger(T::class.java)
    }

    /**
     * Create a logger with the given name.
     */
    public fun makeLogger(label: String?): Logger {
        return modLoggers.getOrPut(label) {
            val labelSuffix = label?.let { "|$it" } ?: ""
            val logger = LogManager.getLogger("$humanName$labelSuffix")
            registerLogger(logger)
            logger
        }
    }

    /**
     * Registers a logger which should be controlled by this mods's debug flag
     */
    public fun registerLogger(logger: Logger) {
        registeredLoggers.add(TrackedLogger(logger, logger.level))
        if (debugEnabled)
            Configurator.setLevel(logger.name, Level.DEBUG)
    }

    private data class TrackedLogger(val logger: Logger, val initialLevel: Level)

    private companion object {
        private val debugPatterns: List<Regex> = System.getProperty("librarianlib.logging.debug", "")
            .split(",")
            .map { glob ->
                Regex.escape(glob.replace("*", "\uE000"))
                    .replace("\uE000", ".*")
                    .toRegex(RegexOption.IGNORE_CASE)
            }
    }
}
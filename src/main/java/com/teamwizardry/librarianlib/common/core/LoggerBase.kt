package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.common.util.toComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class LoggerBase protected constructor(name: String) {
    val debugMode = LibrarianLib.DEV_ENVIRONMENT
    private val logger: Logger

    init {
        logger = LogManager.getLogger(name)
    }

    fun error(message: String, vararg args: Any?) {
        logger.log(Level.ERROR, String.format(message, *args))
    }

    fun error(e: Exception, message: String, vararg args: Any?) {
        logger.log(Level.ERROR, String.format(message, *args))
        e.printStackTrace()
    }

    fun warn(message: String, vararg args: Any?) {
        logger.log(Level.WARN, String.format(message, *args))
    }

    fun info(message: String, vararg args: Any?) {
        logger.log(Level.INFO, String.format(message, *args))
    }

    fun debug(message: String, vararg args: Any?) {
        if (debugMode) logger.log(Level.INFO, String.format(message, *args))
    }

    fun message(player: EntityPlayer, message: String, vararg args: Any?) {
        player.sendStatusMessage(String.format(message, *args).toComponent())
    }

    fun warn(player: EntityPlayer, message: String, vararg args: Any?) {
        player.sendStatusMessage(String.format(message, *args).toComponent().setStyle(Style().setColor(TextFormatting.RED)))
    }

    /**
     * **Only use this if the person seeing it can do something about it**
     *
     * Prints the passed lines to the log, surrounded with asterisks, and immediately dies.
     *
     * Prints: ```
     * ******* **** TITLE **** ********
     * * your lines will appear here, *
     * * each preceded with asterisks *
     * ******* **** TITLE **** ********
     * ```
     *
     * The stars at the end of the lines are controlled with the [endStar] parameter.
     *
     * The title will never print more than 25 characters from the left
     */
    fun bigDie(title: String, lines: List<String>, endStar: Boolean = true) {
        val maxWidth = lines.fold(0, { cur, value -> Math.max(cur, value.length) })

        var titleStarred = " **** $title **** "
        var starPadLeft = (maxWidth + 4 - titleStarred.length) / 2
        if (starPadLeft >= 20)
            starPadLeft = 19
        val starPadRight = (maxWidth + 4 - titleStarred.length) - starPadLeft

        titleStarred = "*" * starPadLeft + titleStarred + "*" * starPadRight

        warn(titleStarred)
        lines.forEach {
            if (endStar) {
                warn("* " + it.padEnd(maxWidth, ' ') + " *")
            } else {
                warn("* " + it)
            }
        }
        warn(titleStarred)
    }
}

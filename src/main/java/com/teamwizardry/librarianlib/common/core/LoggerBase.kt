package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.sendMessage
import com.teamwizardry.librarianlib.common.util.times
import com.teamwizardry.librarianlib.common.util.toComponent
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class LoggerBase protected constructor(name: String) {
    val debugMode = LibrarianLib.DEV_ENVIRONMENT
    private val logger: Logger

    init {
        logger = LogManager.getLogger(name)
    }

    /**
     * Performs special processing on formatting arguments, such as replacing Worlds with "WORLD_NAME (DIM_ID)"
     */
    open fun processFormatArg(value: Any): Any {

        if (value is World) {
            return "${value.providerName} (${value.provider.dimension})"
        }

        return value
    }

    /**
     * Process the passed formatting args, passing each non-null element through [processFormatArg]
     *
     * @return a modified array
     */
    fun processFormatting(value: Array<out Any?>): Array<Any?> {
        val arr = arrayOfNulls<Any?>(value.size)
        for (i in value.indices) {
            val v = value[i]
            if (v != null)
                arr[i] = processFormatArg(v)
            else
                arr[i] = null
        }
        return arr
    }

    fun error(message: String, vararg args: Any?) {
        logger.log(Level.ERROR, String.format(message, *processFormatting(args)))
    }

    fun error(e: Exception, message: String, vararg args: Any?) {
        logger.log(Level.ERROR, String.format(message, *processFormatting(args)))
        e.printStackTrace()
    }

    fun warn(message: String, vararg args: Any?) {
        logger.log(Level.WARN, String.format(message, *processFormatting(args)))
    }

    fun info(message: String, vararg args: Any?) {
        logger.log(Level.INFO, String.format(message, *processFormatting(args)))
    }

    fun debug(message: String, vararg args: Any?) {
        if (debugMode) logger.log(Level.INFO, String.format(message, *processFormatting(args)))
    }

    fun message(player: EntityPlayer, message: String, vararg args: Any?) {
        player.sendMessage(String.format(message, *processFormatting(args)))
    }

    fun warn(player: EntityPlayer, message: String, vararg args: Any?) {
        player.sendStatusMessage(String.format(message, *processFormatting(args)).toComponent().setStyle(Style().setColor(TextFormatting.RED)), false)
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

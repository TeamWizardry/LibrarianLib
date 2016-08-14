package com.teamwizardry.librarianlib.util

import com.teamwizardry.librarianlib.Const
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

abstract class LoggerBase protected constructor(name: String) {
    val debugMode = Const.isDev
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
        if (debugMode) {
            logger.log(Level.INFO, String.format(message, *args))
        }
    }

    fun message(player: EntityPlayer, message: String, vararg args: Any?) {
        player.addChatComponentMessage(TextComponentString(String.format(message, *args)))
    }

    fun warn(player: EntityPlayer, message: String, vararg args: Any?) {
        player.addChatComponentMessage(TextComponentString(String.format(message, *args)).setStyle(Style().setColor(TextFormatting.RED)))
    }
}
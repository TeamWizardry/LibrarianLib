package com.teamwizardry.librarianlib.util;

import com.teamwizardry.librarianlib.Const;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LoggerBase {
    public long prevTicks = -1;
    public boolean debugMode = Const.isDev;
    public boolean doLogging = false;
    private Logger logger;

    { /* basic logging */ }

    { /* chat */ }

    protected LoggerBase(String name) {
        logger = LogManager.getLogger(name);
    }
    
    public abstract LoggerBase getInstance();

    public void error(String message, Object... args) {
        getInstance().logger.log(Level.ERROR, String.format(message, args));
    }

    public void error(Exception e, String message, Object... args) {
        getInstance().logger.log(Level.ERROR, String.format(message, args));
        e.printStackTrace();
    }

    public void warn(String message, Object... args) {
        getInstance().logger.log(Level.WARN, String.format(message, args));
    }

    public void info(String message, Object... args) {
        getInstance().logger.log(Level.INFO, String.format(message, args));
    }

    public void debug(String message, Object... args) {
        if (debugMode) {
            getInstance().logger.log(Level.INFO, String.format(message, args));
        }
    }
    
    public void message(EntityPlayer player, String message, Object... args) {
        player.addChatComponentMessage(new TextComponentString(String.format(message, args)));
    }

    public void warn(EntityPlayer player, String message, Object... args) {
        player.addChatComponentMessage(new TextComponentString(String.format(message, args)).setStyle(new Style().setColor(TextFormatting.RED)));
    }
}
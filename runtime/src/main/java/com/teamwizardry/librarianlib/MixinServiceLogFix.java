package com.teamwizardry.librarianlib;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterDefault;
import org.spongepowered.asm.logging.LoggerAdapterJava;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;

public class MixinServiceLogFix extends MixinServiceModLauncher {
    @Override
    protected ILogger createLogger(String name) {
        if("true".equals(System.getProperty("mixin.agentLogging"))) {
            return new LoggerAdapterJava(name);
        } else {
            return new LoggerAdapterDefault(name);
        }
    }
}

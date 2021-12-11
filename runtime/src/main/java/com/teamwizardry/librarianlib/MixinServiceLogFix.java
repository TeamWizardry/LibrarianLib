package com.teamwizardry.librarianlib;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterConsole;
import org.spongepowered.asm.service.modlauncher.MixinServiceModLauncher;

public class MixinServiceLogFix extends MixinServiceModLauncher {
    @Override
    protected ILogger createLogger(String name) {
        return new LoggerAdapterConsole(name);
    }
}

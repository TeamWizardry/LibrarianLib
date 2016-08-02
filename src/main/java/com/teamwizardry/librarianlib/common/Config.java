package com.teamwizardry.librarianlib.common;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    public static boolean shaders;

    public static void initConfig(File configurationFile) {
        Configuration config = new Configuration(configurationFile);
        config.load();
        shaders = config.get(Configuration.CATEGORY_CLIENT, "shaders", true, "Controls whether LibLib's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with visuals, you may want to turn this off.").getBoolean();
        config.save();
    }

}

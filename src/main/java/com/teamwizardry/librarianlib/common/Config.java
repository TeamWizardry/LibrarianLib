package com.teamwizardry.librarianlib.common;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    public static boolean shaders;

    public static void initConfig(File configurationFile) {
        Configuration config = new Configuration(configurationFile);
        config.load();
        shaders = config.get(Configuration.CATEGORY_CLIENT, "shaders", false, "Controls whether LibLib's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with visuals, you may want to turn this off. If you are experiencing JVM crashes with no errors, disable this as well").getBoolean();
        config.save();
    }

}

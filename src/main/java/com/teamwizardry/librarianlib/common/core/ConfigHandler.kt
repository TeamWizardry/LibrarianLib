package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.common.util.ConfigPropertyBoolean
import net.minecraftforge.common.config.Configuration

object ConfigHandler {
    @ConfigPropertyBoolean(Configuration.CATEGORY_CLIENT, "shaders", "Controls whether LibLib's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with visuals, you may want to turn this off.", false)
    @JvmStatic
    var useShaders: Boolean = false

    @ConfigPropertyBoolean(Configuration.CATEGORY_CLIENT, "generateJson", "Controls whether LibLib autogenerates json.", true, true)
    @JvmStatic
    var generateJson = true

    @ConfigPropertyBoolean(Configuration.CATEGORY_CLIENT, "autoSaveTEs", "Controls whether LibLib autosaves compatible TEs.", true)
    @JvmStatic
    var autoSaveTEs = true

}

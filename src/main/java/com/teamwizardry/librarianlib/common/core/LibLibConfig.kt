package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.ConfigPropertyBoolean
import net.minecraftforge.common.config.Configuration

object LibLibConfig {
    @JvmStatic
    @ConfigPropertyBoolean(LibrarianLib.MODID, Configuration.CATEGORY_CLIENT, "shaders", "Controls whether LibLib's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with visuals, you may want to turn this off.", false)
    var useShaders: Boolean = false

    @JvmStatic
    @ConfigPropertyBoolean(LibrarianLib.MODID, Configuration.CATEGORY_CLIENT, "generateJson", "Controls whether LibLib autogenerates json.", true, true)
    var generateJson = true

    @JvmStatic
    @ConfigPropertyBoolean(LibrarianLib.MODID, Configuration.CATEGORY_GENERAL, "generateTestBlock", "Controls whether LibLib creates a test block.", false, true)
    var generateTestBlock = false

}

package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraftforge.common.config.Configuration

import java.io.File

object ConfigHandler {
    @JvmStatic
    var useShaders: Boolean = false

    var generateJson = true

    fun initConfig(configurationFile: File) {
        val config = Configuration(configurationFile)
        config.load()
        useShaders = config.get(Configuration.CATEGORY_CLIENT, "shaders", useShaders, "Controls whether LibLib's shaders are used. If you're using the GLSL Shaders mod and are having graphical troubles with visuals, you may want to turn this off.").boolean

        if (LibrarianLib.DEV_ENVIRONMENT) generateJson = config.get(Configuration.CATEGORY_CLIENT, "generateJson", generateJson, "Controls whether LibLib autogenerates json.").boolean
        config.save()
    }

}

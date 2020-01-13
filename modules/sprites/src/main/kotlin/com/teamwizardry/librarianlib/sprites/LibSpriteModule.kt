package com.teamwizardry.librarianlib.sprites

import com.teamwizardry.librarianlib.LibrarianLibModule
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

object LibSpritesModule : LibrarianLibModule("sprites", logger) {
    override fun clientSetup(event: FMLClientSetupEvent) {
        Texture.registerReloadHandler()
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites")

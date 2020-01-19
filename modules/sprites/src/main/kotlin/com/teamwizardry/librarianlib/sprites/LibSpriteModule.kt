package com.teamwizardry.librarianlib.sprites

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager

object LibSpritesModule : LibrarianLibModule("sprites", logger) {
    override fun clientSetup(event: FMLClientSetupEvent) {
        Client.resourceReloadHandler.register(SpritesheetLoader)
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Sprites")

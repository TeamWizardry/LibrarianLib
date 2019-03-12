package com.teamwizardry.librarianlib.test.gui

import com.teamwizardry.librarianlib.features.gui.hud.GuiHud
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object GuiEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        GuiItems
        ClientRunnable.run {
            GuiHud
        }
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {

    }
}

object GuiItems {
    val guiitem = ItemGuiOpener()
}

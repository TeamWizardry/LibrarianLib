package com.teamwizardry.librarianlib.test.neogui

import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import com.teamwizardry.librarianlib.test.testcore.TestEntryPoint
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by TheCodeWarrior
 */
object NeoGuiEntryPoint : TestEntryPoint {
    override fun preInit(event: FMLPreInitializationEvent) {
        NeoGuiItems
    }

    override fun init(event: FMLInitializationEvent) {

    }

    override fun postInit(event: FMLPostInitializationEvent) {
        ClientRunnable.run {
            HudTest
        }
    }
}

object NeoGuiItems {
    val guiitem = ItemGuiOpener()
}

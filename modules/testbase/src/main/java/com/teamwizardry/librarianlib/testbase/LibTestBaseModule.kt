package com.teamwizardry.librarianlib.testbase

import com.teamwizardry.librarianlib.core.LibrarianLibModule
import com.teamwizardry.librarianlib.testbase.objects.TestItemConfig
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-testbase")
class LibTestBaseModule : LibrarianLibModule("testbase", logger) {

    override fun registerItems(itemRegistryEvent: RegistryEvent.Register<Item>) {
    }
}

internal val logger = LogManager.getLogger("LibrarianLib/Test Base")

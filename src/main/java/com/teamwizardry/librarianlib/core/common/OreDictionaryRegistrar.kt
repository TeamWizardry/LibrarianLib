package com.teamwizardry.librarianlib.core.common

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
object OreDictionaryRegistrar {
    val toRegister = mutableMapOf<() -> ItemStack, String>()

    @JvmStatic
    fun registerOre(name: String, stack: () -> ItemStack) {
        toRegister[stack] = name
    }

    @JvmStatic
    fun registerOre(name: String, item: Item) = registerOre(name) { ItemStack(item) }

    @JvmStatic
    fun registerOre(name: String, block: Block) = registerOre(name) { ItemStack(block) }

    @JvmStatic
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun init(e: RegistryEvent.Register<Item>) = toRegister.forEach { stack, key -> OreDictionary.registerOre(key, stack()) }
}

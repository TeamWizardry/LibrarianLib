package com.teamwizardry.librarianlib.core.common

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
object OreDictionaryRegistrar {
    val toRegister = mutableMapOf<() -> ItemStack, String>()

    @JvmStatic
    fun registerOre(name: String, stack: () -> ItemStack) {
        toRegister.put(stack, name)
    }

    @JvmStatic
    fun registerOre(name: String, item: Item) = registerOre(name, { ItemStack(item) })

    @JvmStatic
    fun registerOre(name: String, block: Block) = registerOre(name, { ItemStack(block) })

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @Suppress("UNUSED_PARAMETER")
    fun init(e: RegistryEvent.Register<Item>) = toRegister.forEach { stack, key -> OreDictionary.registerOre(key, stack()) }
}

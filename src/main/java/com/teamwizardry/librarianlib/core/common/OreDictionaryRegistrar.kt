package com.teamwizardry.librarianlib.core.common

import com.teamwizardry.librarianlib.core.LibrarianLib
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
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
    val furnaceRecipeStacks = mutableMapOf<() -> ItemStack, Pair<() -> ItemStack, Float>>()
    val furnaceRecipeItems = mutableMapOf<() -> Item, Pair<() -> ItemStack, Float>>()
    val furnaceRecipeBlocks = mutableMapOf<() -> Block, Pair<() -> ItemStack, Float>>()

    @JvmStatic
    fun registerOre(name: String, stack: () -> ItemStack) {
        toRegister[stack] = name
    }

    @JvmStatic
    fun registerOre(name: String, item: Item) = registerOre(name) { ItemStack(item) }

    @JvmStatic
    fun registerOre(name: String, block: Block) = registerOre(name) { ItemStack(block) }

    @JvmStatic
    fun registerSmeltingItem(item: () -> Item, output: () -> ItemStack, xp: Float) {
        furnaceRecipeItems[item] = output to xp
    }

    @JvmStatic
    fun registerSmeltingItem(item: Item, output: () -> ItemStack, xp: Float) = registerSmeltingItem({ item }, output, xp)

    @JvmStatic
    fun registerSmeltingStack(item: () -> ItemStack, output: () -> ItemStack, xp: Float) {
        furnaceRecipeStacks[item] = output to xp
    }

    @JvmStatic
    fun registerSmeltingStack(item: ItemStack, output: () -> ItemStack, xp: Float) = registerSmeltingStack({ item }, output, xp)

    @JvmStatic
    fun registerSmeltingBlock(item: () -> Block, output: () -> ItemStack, xp: Float) {
        furnaceRecipeBlocks[item] = output to xp
    }

    @JvmStatic
    fun registerSmeltingBlock(item: Block, output: () -> ItemStack, xp: Float) = registerSmeltingBlock({ item }, output, xp)

    @JvmStatic
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun init(e: RegistryEvent.Register<Item>) {
        toRegister.forEach { stack, key -> OreDictionary.registerOre(key, stack()) }
        furnaceRecipeStacks.forEach { stack, out -> FurnaceRecipes.instance()
                .addSmeltingRecipe(stack(), out.first(), out.second)}
        furnaceRecipeItems.forEach { item, out -> FurnaceRecipes.instance()
                .addSmelting(item(), out.first(), out.second)}
        furnaceRecipeBlocks.forEach { block, out -> FurnaceRecipes.instance()
                .addSmeltingRecipeForBlock(block(), out.first(), out.second)}
    }
}

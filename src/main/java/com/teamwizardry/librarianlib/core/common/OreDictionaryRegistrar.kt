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
import java.util.LinkedList

/**
 * @author WireSegal
 * Created at 1:45 PM on 6/24/17.
 */
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
object OreDictionaryRegistrar {
    private val toRegister = LinkedList<Pair<() -> ItemStack, String>>()
    private val furnaceRecipeStacks = LinkedList<Triple<() -> ItemStack, () -> ItemStack, Float>>()
    private val furnaceRecipeItems = LinkedList<Triple<() -> Item, () -> ItemStack, Float>>()
    private val furnaceRecipeBlocks = LinkedList<Triple<() -> Block, () -> ItemStack, Float>>()

    @JvmStatic
    fun registerOre(name: String, stack: () -> ItemStack) {
        toRegister.add(stack to name)
    }

    @JvmStatic
    fun registerOre(name: String, item: Item) = registerOre(name) { ItemStack(item) }

    @JvmStatic
    fun registerOre(name: String, block: Block) = registerOre(name) { ItemStack(block) }

    @JvmStatic
    fun registerSmeltingItem(item: () -> Item, output: () -> ItemStack, xp: Float) {
        furnaceRecipeItems.add(Triple(item, output, xp))
    }

    @JvmStatic
    fun registerSmeltingItem(item: Item, output: () -> ItemStack, xp: Float) = registerSmeltingItem({ item }, output, xp)

    @JvmStatic
    fun registerSmeltingStack(item: () -> ItemStack, output: () -> ItemStack, xp: Float) {
        furnaceRecipeStacks.add(Triple(item, output, xp))
    }

    @JvmStatic
    fun registerSmeltingStack(item: ItemStack, output: () -> ItemStack, xp: Float) = registerSmeltingStack({ item }, output, xp)

    @JvmStatic
    fun registerSmeltingBlock(item: () -> Block, output: () -> ItemStack, xp: Float) {
        furnaceRecipeBlocks.add(Triple(item, output, xp))
    }

    @JvmStatic
    fun registerSmeltingBlock(item: Block, output: () -> ItemStack, xp: Float) = registerSmeltingBlock({ item }, output, xp)

    @JvmStatic
    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun init(e: RegistryEvent.Register<Item>) {
        toRegister.forEach { (stack, key) -> OreDictionary.registerOre(key, stack()) }
        furnaceRecipeStacks.forEach { (stack, output, xp) ->
            FurnaceRecipes.instance()
                .addSmeltingRecipe(stack(), output(), xp)
        }
        furnaceRecipeItems.forEach { (item, output, xp) ->
            FurnaceRecipes.instance()
                .addSmelting(item(), output(), xp)
        }
        furnaceRecipeBlocks.forEach { (block, output, xp) ->
            FurnaceRecipes.instance()
                .addSmeltingRecipeForBlock(block(), output(), xp)
        }

        // garbage collect the stuff
        toRegister.clear()
        furnaceRecipeStacks.clear()
        furnaceRecipeItems.clear()
        furnaceRecipeBlocks.clear()
    }
}

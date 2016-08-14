package com.teamwizardry.librarianlib.util

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry

object RegistryUtils {
    /**
     * Registers a [Block] with the [GameRegistry] and creates a standard ItemBlock for it
     * @param block - the block to be registered
     * *
     * @param modid - the modid of the mod registering the block
     * *
     * @param name - the name the block and itemblock should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in block, registered and with it's registry name and unlocalised name set
     */
    fun registerBlockAndCreateIB(block: Block, modid: String, name: String): Block {
        return registerBlock(block, ItemBlock(block), modid, name)
    }

    /**
     * Registers a [Block] with the [GameRegistry] with no ItemBlock
     * @param block - the block to be registered
     * *
     * @param modid - the modid of the mod registering the block
     * *
     * @param name - the name the block should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in block, registered and with it's registry name and unlocalised name set
     */
    fun registerBlockWithNoIB(block: Block, modid: String, name: String): Block {
        return registerBlock(block, null, modid, name)
    }

    /**
     * Registers a [Block] and an [ItemBlock] with the [GameRegistry]
     * @param block - the block to be registered
     * *
     * @param itemBlock - the itemblock to be registered
     * *
     * @param modid - the modid of the mod registering the block
     * *
     * @param name - the name the block and itemblock should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in block, registered and with it's registry name and unlocalised name set
     */
    fun registerBlock(block: Block, itemBlock: ItemBlock?, modid: String, name: String): Block {
        block.setRegistryName(name)
        block.unlocalizedName = modid + "." + name
        GameRegistry.register(block)
        if (itemBlock != null) {
            itemBlock.setRegistryName(name)
            GameRegistry.register(itemBlock)
        }
        return block
    }

    /**
     * Registers an [Item] with the [GameRegistry]
     * @param item - the item to be registered
     * *
     * @param modid - the modid of the mod registering the item
     * *
     * @param name - the name the item should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in item, registered and with it's registry name and unlocalised name set
     */
    fun registerItem(item: Item, modid: String, name: String): Item {
        item.setRegistryName(name)
        item.unlocalizedName = modid + "." + name
        GameRegistry.register(item)
        return item
    }

    /**
     * Creates a generic block with no special properties. This method is useful for creating resource blocks or any
     * other block that doesn't need any special behaviour.
     * IMPORTANT: The block is not registered; this is so that it can be used as input to other helper methods, such as
     * [RegistryUtils.registerBlock] or [RegistryUtils.registerBlockAndCreateIB]
     * @param material - the [Material] the block should use
     * *
     * @param modid - the modid of the mod registering the block
     * *
     * @param name - the name the block should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in block, with it's registry name and unlocalised name set
     */
    fun createGenericBlock(material: Material, modid: String, name: String): Block {
        return Block(material).setRegistryName(name).setUnlocalizedName(modid + "." + name)
    }

    /**
     * Creates a generic block with no special properties. This method is useful for creating resource items or any
     * other item that doesn't need any special behaviour.
     * IMPORTANT: The item is not registered; this is so that it can be used as input to other helper methods, such as
     * [RegistryUtils.registerItem]
     * @param modid - the modid of the mod registering the item
     * *
     * @param name - the name the item should be registered under. This will be used as the unlocalised name, with the modid prepended
     * *
     * @return the passed in item, with it's registry name and unlocalised name set
     */
    fun createGenericItem(modid: String, name: String): Item {
        return Item().setRegistryName(name).setUnlocalizedName(modid + "." + name)
    }
}

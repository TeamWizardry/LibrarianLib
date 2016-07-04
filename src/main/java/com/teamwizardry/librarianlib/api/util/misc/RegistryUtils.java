package com.teamwizardry.librarianlib.api.util.misc;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegistryUtils 
{
	/**
	 * Registers a {@link Block} with the {@link GameRegistry} and creates a standard ItemBlock for it
	 * @param block - the block to be registered
	 * @param modid - the modid of the mod registering the block
	 * @param name - the name the block and itemblock should be registered under. This will be used as the unlocalised name, with the modid prepended 
	 * @return the passed in block, registered and with it's registry name and unlocalised name set
	 */
	public static Block registerBlockAndCreateIB(Block block, String modid, String name)
	{
		return registerBlock(block, new ItemBlock(block), modid, name);
	}
	
	/**
	 * Registers a {@link Block} with the {@link GameRegistry} with no ItemBlock
	 * @param block - the block to be registered
	 * @param modid - the modid of the mod registering the block
	 * @param name - the name the block should be registered under. This will be used as the unlocalised name, with the modid prepended 
	 * @return the passed in block, registered and with it's registry name and unlocalised name set
	 */
	public static Block registerBlockWithNoIB(Block block, String modid, String name)
	{
		return registerBlock(block, null, modid, name);
	}

	/**
	 * Registers a {@link Block} and an {@link ItemBlock} with the {@link GameRegistry}
	 * @param block - the block to be registered
	 * @param itemBlock - the itemblock to be registered
	 * @param modid - the modid of the mod registering the block
	 * @param name - the name the block and itemblock should be registered under. This will be used as the unlocalised name, with the modid prepended 
	 * @return the passed in block, registered and with it's registry name and unlocalised name set
	 */
	public static Block registerBlock(Block block, ItemBlock itemBlock, String modid, String name)
	{
		block.setRegistryName(name);
		block.setUnlocalizedName(modid + "." + name);
		GameRegistry.register(block);
		if(itemBlock != null)
		{
			itemBlock.setRegistryName(name);
			GameRegistry.register(itemBlock);
		}
		return block;
	}
	
	/**
	 * Registers an {@link Item} with the {@link GameRegistry}
	 * @param item - the item to be registered
	 * @param modid - the modid of the mod registering the item
	 * @param name - the name the item should be registered under. This will be used as the unlocalised name, with the modid prepended 
	 * @return the passed in item, registered and with it's registry name and unlocalised name set
	 */
	public static Item registerItem(Item item, String modid, String name)
	{
		item.setRegistryName(name);
		item.setUnlocalizedName(modid + "." + name);
		GameRegistry.register(item);
		return item;
	}
	
	/**
	 * Creates a generic block with no special properties. This method is useful for creating resource blocks or any
	 * other block that doesn't need any special behaviour.
	 * IMPORTANT: The block is not registered; this is so that it can be used as input to other helper methods, such as
	 * {@link RegistryUtils#registerBlock(Block, ItemBlock, String, String)} or {@link RegistryUtils#registerBlockAndCreateIB(Block, String, String)}
	 * @param material - the {@link Material} the block should use
	 * @param modid - the modid of the mod registering the block
	 * @param name - the name the block should be registered under. This will be used as the unlocalised name, with the modid prepended  
	 * @return the passed in block, with it's registry name and unlocalised name set
	 */
	public static Block createGenericBlock(Material material, String modid, String name)
	{
		return new Block(material).setRegistryName(name).setUnlocalizedName(modid + "." + name);
	}
	
	/**
	 * Creates a generic block with no special properties. This method is useful for creating resource items or any
	 * other item that doesn't need any special behaviour.
	 * IMPORTANT: The item is not registered; this is so that it can be used as input to other helper methods, such as
	 * {@link RegistryUtils#registerItem(Item, String, String)} 
	 * @param modid - the modid of the mod registering the item
	 * @param name - the name the item should be registered under. This will be used as the unlocalised name, with the modid prepended  
	 * @return the passed in item, with it's registry name and unlocalised name set
	 */
	public static Item createGenericItem(String modid, String name)
	{
		return new Item().setRegistryName(name).setUnlocalizedName(modid + "." + name);
	}
}

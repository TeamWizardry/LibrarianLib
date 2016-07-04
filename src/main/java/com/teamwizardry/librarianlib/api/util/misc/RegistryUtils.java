package com.teamwizardry.librarianlib.api.util.misc;

import net.minecraft.block.Block;
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
	 * 
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
}

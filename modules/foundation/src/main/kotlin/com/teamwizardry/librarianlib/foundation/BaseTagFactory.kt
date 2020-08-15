package com.teamwizardry.librarianlib.foundation

import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.tags.BlockTags
import net.minecraft.tags.EntityTypeTags
import net.minecraft.tags.FluidTags
import net.minecraft.tags.ItemTags
import net.minecraft.tags.Tag
import net.minecraft.util.ResourceLocation

class BaseTagFactory(val modid: String) {
    /**
     * create a new Block tag
     */
    fun block(name: String): Tag<Block> = BlockTags.Wrapper(ResourceLocation(modid, name))

    /**
     * Create a new EntityType tag
     */
    fun entityType(name: String): Tag<EntityType<*>> = EntityTypeTags.Wrapper(ResourceLocation(modid, name))

    /**
     * Create a new Fluid tag
     */
    fun fluid(name: String): Tag<Fluid> = FluidTags.Wrapper(ResourceLocation(modid, name))

    /**
     * Create a new Item tag
     */
    fun item(name: String): Tag<Item> = ItemTags.Wrapper(ResourceLocation(modid, name))
}
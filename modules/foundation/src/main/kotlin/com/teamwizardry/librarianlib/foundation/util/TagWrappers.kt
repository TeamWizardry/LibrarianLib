package com.teamwizardry.librarianlib.foundation.util

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

/**
 * A utility class for easily creating tags.
 *
 * **Note:** You'll almost always want to create an item tag when you create a block tag. You can do this using
 * [itemFormOf], and you can specify their equivalence using
 * `registrationManager.datagen.blockTags.addItemForm(blockTag, itemTag)`
 */
public object TagWrappers {
    /**
     * Creates a tag for the item form of the given block. i.e. creates an item tag with the same ID as the block tag.
     */
    @JvmStatic
    public fun itemFormOf(blockTag: Tag<Block>): Tag<Item> = item(blockTag.id)

    @JvmStatic
    public fun block(name: String): Tag<Block> = BlockTags.Wrapper(ResourceLocation(name))

    @JvmStatic
    public fun entityType(name: String): Tag<EntityType<*>> = EntityTypeTags.Wrapper(ResourceLocation(name))

    @JvmStatic
    public fun fluid(name: String): Tag<Fluid> = FluidTags.Wrapper(ResourceLocation(name))

    @JvmStatic
    public fun item(name: String): Tag<Item> = ItemTags.Wrapper(ResourceLocation(name))

    @JvmStatic
    public fun block(modid: String, name: String): Tag<Block> = block(ResourceLocation(modid, name))

    @JvmStatic
    public fun entityType(modid: String, name: String): Tag<EntityType<*>> = entityType(ResourceLocation(modid, name))

    @JvmStatic
    public fun fluid(modid: String, name: String): Tag<Fluid> = fluid(ResourceLocation(modid, name))

    @JvmStatic
    public fun item(modid: String, name: String): Tag<Item> = item(ResourceLocation(modid, name))

    @JvmStatic
    public fun block(name: ResourceLocation): Tag<Block> = BlockTags.Wrapper(name)

    @JvmStatic
    public fun entityType(name: ResourceLocation): Tag<EntityType<*>> = EntityTypeTags.Wrapper(name)

    @JvmStatic
    public fun fluid(name: ResourceLocation): Tag<Fluid> = FluidTags.Wrapper(name)

    @JvmStatic
    public fun item(name: ResourceLocation): Tag<Item> = ItemTags.Wrapper(name)
}
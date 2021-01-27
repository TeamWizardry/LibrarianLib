package com.teamwizardry.librarianlib.foundation.util

import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.tags.*
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
    public fun itemFormOf(blockTag: ITag.INamedTag<Block>): ITag.INamedTag<Item> = item(blockTag.name)

    @JvmStatic
    public fun block(name: String): ITag.INamedTag<Block> = BlockTags.makeWrapperTag(name)

    @JvmStatic
    public fun entityType(name: String): ITag.INamedTag<EntityType<*>> = EntityTypeTags.getTagById(name)

    @JvmStatic
    public fun fluid(name: String): ITag.INamedTag<Fluid> = FluidTags.makeWrapperTag(name)

    @JvmStatic
    public fun item(name: String): ITag.INamedTag<Item> = ItemTags.makeWrapperTag(name)

    @JvmStatic
    public fun block(modid: String, name: String): ITag.INamedTag<Block> = block(ResourceLocation(modid, name))

    @JvmStatic
    public fun entityType(modid: String, name: String): ITag.INamedTag<EntityType<*>> = entityType(ResourceLocation(modid, name))

    @JvmStatic
    public fun fluid(modid: String, name: String): ITag.INamedTag<Fluid> = fluid(ResourceLocation(modid, name))

    @JvmStatic
    public fun item(modid: String, name: String): ITag.INamedTag<Item> = item(ResourceLocation(modid, name))

    @JvmStatic
    public fun block(name: ResourceLocation): ITag.INamedTag<Block> = BlockTags.makeWrapperTag(name.toString())

    @JvmStatic
    public fun entityType(name: ResourceLocation): ITag.INamedTag<EntityType<*>> = EntityTypeTags.getTagById(name.toString())

    @JvmStatic
    public fun fluid(name: ResourceLocation): ITag.INamedTag<Fluid> = FluidTags.makeWrapperTag(name.toString())

    @JvmStatic
    public fun item(name: ResourceLocation): ITag.INamedTag<Item> = ItemTags.makeWrapperTag(name.toString())
}
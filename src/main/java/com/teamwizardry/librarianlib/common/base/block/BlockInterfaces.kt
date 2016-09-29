package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.base.IVariantHolder
import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface IModBlockProvider : IVariantHolder {

    /**
     * Provides a block instance to use for registration. This does not have to be the same instance as the IModBlockProvider.
     */
    val providedBlock: Block

    /**
     * A list of IProperties to ignore in a blockstate file.
     * If getStateMapper is overridden, this will have to be implemented in the overriden implementation.
     */
    val ignoredProperties: Array<IProperty<*>>?
        get() = null

    /**
     * Provides a statemapper for the block, if needed. By default uses ignored properties only. Leave null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getStateMapper(): IStateMapper? {
        val ignored = ignoredProperties
        return if (ignored == null || ignored.isEmpty()) null else StateMap.Builder().ignore(*ignored).build()
    }
}

/**
 * An interface which defines a block which can be used with an ItemModBlock.
 */
interface IModBlock : IModBlockProvider {

    /**
     * The name of the block.
     */
    val bareName: String

    /**
     * The rarity of the block, for the ItemModBlock.
     */
    fun getBlockRarity(stack: ItemStack): EnumRarity {
        return EnumRarity.COMMON
    }

    /**
     * Provides a mesh definition which can override the model loaded based on the MRL you pass back. Leave null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getCustomMeshDefinition(): ItemMeshDefinition? {
        return null
    }

    /**
     * The tab to register the block in. Leave null to not register.
     */
    val creativeTab: ModCreativeTab?
        get() = null

    /**
     * All IModBlock instances must extend Block.
     */
    override val providedBlock: Block
        get() = this as Block
}

/**
 * An interface which defines a block provider (not necessarily with an item) that colorizes the block.
 */
interface IBlockColorProvider : IItemColorProvider, IModBlockProvider {
    /**
     * Provides a block color for the provided block. Leave null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getBlockColor(): IBlockColor?

    @SideOnly(Side.CLIENT)
    override fun getItemColor(): IItemColor? = null
}

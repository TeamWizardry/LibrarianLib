package com.teamwizardry.librarianlib.common.base.item

import com.teamwizardry.librarianlib.common.base.IVariantHolder
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Defines a mod item holder. This can be used to wrap in the model handling of ZML without any of the other registration.
 */
interface IModItemProvider : IVariantHolder {

    /**
     * Provides an item instance to use for registration. This does not have to be the same instance as the IModItemProvider.
     */
    val providedItem: Item

    /**
     * Provides a mesh definition which can override the model loaded based on the MRL you pass back. Leave null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getCustomMeshDefinition(): ItemMeshDefinition? {
        return null
    }
}

/**
 * An interface which defines an item provider (or an IModBlock with an item) that colorizes the item.
 */
interface IItemColorProvider : IVariantHolder {
    /**
     * Provides an item color for the provided item. Leave null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getItemColor(): IItemColor?
}

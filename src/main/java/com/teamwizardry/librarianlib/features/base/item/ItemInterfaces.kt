package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.base.IVariantHolder
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
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
    val meshDefinition: ((stack: ItemStack) -> ModelResourceLocation)?
        get() = null
}

/**
 * An interface which defines an item provider (or an IModBlock with an item) that colorizes the item.
 */
interface IItemColorProvider : IVariantHolder {
    /**
     * Provides an item colorPrimary for the provided item. Leave null to use default behavior.
     */
    val itemColorFunction: ((stack: ItemStack, tintIndex: Int) -> Int)?
}

/**
 * An interface which defines an item provider (or an IModBlock with an item) that has an IPerspectiveAwareModel.
 */
interface ISpecialModelProvider : IModItemProvider {
    /**
     * Provides a special model for the provided item. Return null to use default behavior.
     */
    @SideOnly(Side.CLIENT)
    fun getSpecialModel(index: Int): IBakedModel?
}

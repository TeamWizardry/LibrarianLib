package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.util.VariantHelper
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.Loader

/**
 * The default implementation for an IModBlock.
 */
open class BlockMod(name: String, materialIn: Material, color: MapColor, vararg variants: String) : Block(materialIn, color), IModBlock {

    constructor(name: String, materialIn: Material, vararg variants: String) : this(name, materialIn, materialIn.materialMapColor, *variants)

    override val variants: Array<out String>

    override val bareName: String = name
    val modId: String

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        modId = Loader.instance().activeModContainer().modId
        this.variants = VariantHelper.beginSetupBlock(name, variants)
        VariantHelper.finishSetupBlock(this, name, itemForm, creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return super.setUnlocalizedName(name)
    }

    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }
}

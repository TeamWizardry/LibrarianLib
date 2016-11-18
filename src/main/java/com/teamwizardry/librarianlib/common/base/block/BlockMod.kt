package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.util.VariantHelper
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoAccessor
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.world.World
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe", striprefs = true)
open class BlockMod(name: String, materialIn: Material, color: MapColor, vararg variants: String) : Block(materialIn, color), IModBlock, IProbeInfoAccessor {

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
        return this
    }

    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }


    @Optional.Method(modid = "theoneprobe")
    override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        //NO-OP
    }

    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]
}

package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * @author WireSegal
 * Created at 10:29 PM on 5/27/16.
 */
@Suppress("LeakingThis")
open class BlockModPlanks(name: String, vararg variants: String) : BlockMod(name, Material.WOOD, *variants) {
    init {
        soundType = SoundType.WOOD
        setHardness(2f)
        setResistance(5f)
        val form = itemForm
        if (form != null)
            for (variant in this.variants.indices)
                OreDictionaryRegistrar.registerOre("plankWood") { ItemStack(form, 1, variant) }
    }

    override fun getHarvestTool(state: IBlockState?): String? {
        return "axe"
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return type == "axe"
    }

    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 20
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5
}

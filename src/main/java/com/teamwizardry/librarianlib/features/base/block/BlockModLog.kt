package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 10:36 AM on 5/7/16.
 */
@Suppress("LeakingThis")
open class BlockModLog(name: String, vararg variants: String) : BlockModPillar(name, Material.WOOD, *variants) {

    init {
        blockHardness = 2.0f
        soundType = SoundType.WOOD
        if (itemForm != null) {
            for (variant in this.variants.indices)
                OreDictionaryRegistrar.registerOre("logWood") { ItemStack(itemForm, 1, variant) }
            FurnaceRecipes.instance().addSmeltingRecipeForBlock(this, ItemStack(Items.COAL, 1, 1), 0.15f)
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i = 4
        val j = i + 1

        if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
            for (blockpos in BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                val iblockstate = worldIn.getBlockState(blockpos)

                if (iblockstate.block.isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.block.beginLeavesDecay(iblockstate, worldIn, blockpos)
                }
            }
        }
    }

    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5

    override fun canSustainLeaves(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun isWood(world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getHarvestTool(state: IBlockState?): String? {
        return "axe"
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return type == "axe"
    }

    override val postFix: String
        get() = "bark"
}

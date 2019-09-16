package com.teamwizardry.librarianlib.features.structure

import com.teamwizardry.librarianlib.features.structure.Structure.Companion.blocks
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.IBlockAccess
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.structure.template.Template
import net.minecraft.world.gen.structure.template.Template.BlockInfo
import java.util.*
import kotlin.math.max

/**
 * Used to access the block in a template. Made for getActualState()

 * @author Pierce Corcoran
 */
open class TemplateBlockAccess(protected var template: Template?) : IBlockAccess {
    protected var templateBlocks: List<BlockInfo>? = null
    protected var overrides: MutableMap<BlockPos, IBlockState> = HashMap()

    init {
        templateBlocks = template?.blocks

    }

    fun setBlockState(pos: BlockPos, state: IBlockState) {
        overrides.put(pos, state)
    }

    fun resetSetBlocks() {
        overrides.clear()
    }

    fun getBlockStateOrNull(pos: BlockPos): IBlockState? {
        if (overrides.containsKey(pos) && overrides[pos] != null)
            return overrides[pos]

        if (template == null || templateBlocks == null)
            return null

        val state: IBlockState? = templateBlocks!!
                .firstOrNull { it.pos == pos }
                ?.blockState

        return state
    }

    override fun getTileEntity(pos: BlockPos): TileEntity? {
        return null
    }

    override fun getCombinedLight(pos: BlockPos, lightValue: Int): Int {
        val sky = EnumSkyBlock.SKY.defaultLightValue
        val block = max(EnumSkyBlock.BLOCK.defaultLightValue, lightValue)
        return (sky shl 20) or (block shl 4)
    }

    override fun getBlockState(pos: BlockPos): IBlockState {
        val state = getBlockStateOrNull(pos)
        return state ?: Blocks.AIR.defaultState
    }

    override fun isAirBlock(pos: BlockPos): Boolean {
        return getBlockState(pos) === Blocks.AIR.defaultState
    }

    override fun getStrongPower(pos: BlockPos, direction: EnumFacing): Int {
        return 0
    }

    override fun getWorldType(): WorldType {
        return WorldType.CUSTOMIZED
    }

    override fun isSideSolid(pos: BlockPos, side: EnumFacing, _default: Boolean): Boolean {
        if (template == null || templateBlocks == null)
            return _default
        val state = getBlockStateOrNull(pos) ?: return _default

        return state.isSideSolid(this, pos, side)
    }

    override fun getBiome(pos: BlockPos): Biome {
        return Biomes.PLAINS
    }

}

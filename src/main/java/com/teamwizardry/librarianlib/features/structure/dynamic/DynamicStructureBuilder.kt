package com.teamwizardry.librarianlib.features.structure.dynamic

import com.teamwizardry.librarianlib.features.base.RegistryMod
import com.teamwizardry.librarianlib.features.helpers.currentModId
import io.netty.util.collection.LongObjectHashMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper

/**
 * @author WireSegal
 * Created at 1:33 PM on 3/29/18.
 */

private val NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000))
private val NUM_Z_BITS = NUM_X_BITS
private val NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS
private const val Z_SHIFT = 0
private val Y_SHIFT = Z_SHIFT + NUM_Z_BITS
private val X_SHIFT = Y_SHIFT + NUM_Y_BITS
private val X_MASK = (1L shl NUM_X_BITS) - 1L
private val Y_MASK = (1L shl NUM_Y_BITS) - 1L
private val Z_MASK = (1L shl NUM_Z_BITS) - 1L

fun toLong(px: Int, py: Int, pz: Int) =
        (px.toLong() and X_MASK shl X_SHIFT) or
        (py.toLong() and Y_MASK shl Y_SHIFT) or
        (pz.toLong() and Z_MASK shl Z_SHIFT)

fun fromLongX(pack: Long)
        = (pack shl (64 - X_SHIFT - NUM_X_BITS) shr (64 - NUM_X_BITS)).toInt()
fun fromLongY(pack: Long)
        = (pack shl (64 - Y_SHIFT - NUM_Y_BITS) shr (64 - NUM_Y_BITS)).toInt()
fun fromLongZ(pack: Long)
        = (pack shl (64 - Z_SHIFT - NUM_Z_BITS) shr (64 - NUM_Z_BITS)).toInt()

class DynamicStructureBuilder {
    private val packed = LongObjectHashMap<DynamicBlockInfo>()
    private var finalized = false

    fun addBlock(px: Int, py: Int, pz: Int, info: DynamicBlockInfo): DynamicStructureBuilder {
        assert(!finalized) { "Already built!" }
        packed[toLong(px, py, pz)] = info
        return this
    }

    fun addBlock(px: Int, py: Int, pz: Int, vararg states: IBlockState) = addBlock(px, py, pz,
            if (states.size == 1) SingleState(states.first()) else OfStates(*states))
    fun addBlock(px: Int, py: Int, pz: Int, vararg blocks: Block) = addBlock(px, py, pz,
            if (blocks.size == 1) SingleBlock(blocks.first()) else OfBlocks(*blocks))

    fun build(name: String) = build(ResourceLocation(currentModId, name))

    fun build(name: ResourceLocation) {
        assert(!finalized) { "Already built!" }
        finalized = true
        STRUCTURE_REGISTRY.register(name, DynamicStructure(packed))
    }

    companion object {
        fun addBlock(px: Int, py: Int, pz: Int, info: DynamicBlockInfo)
                = DynamicStructureBuilder().addBlock(px, py, pz, info)

        fun addBlock(px: Int, py: Int, pz: Int, vararg states: IBlockState)
                = DynamicStructureBuilder().addBlock(px, py, pz, *states)

        fun addBlock(px: Int, py: Int, pz: Int, vararg blocks: Block)
                = DynamicStructureBuilder().addBlock(px, py, pz, *blocks)

    }
}

val STRUCTURE_REGISTRY = RegistryMod<DynamicStructure>()

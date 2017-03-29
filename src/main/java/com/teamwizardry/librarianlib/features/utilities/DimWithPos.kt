package com.teamwizardry.librarianlib.features.utilities

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 5:29 PM on 11/17/16.
 */
data class DimWithPos(val dim: Int, val pos: BlockPos) {
    constructor(world: World, pos: BlockPos) : this(world.provider.dimension, pos)

    override fun toString(): String {
        return "$dim:${pos.x}:${pos.y}:${pos.z}"
    }

    companion object {
        @JvmStatic
        fun fromString(s: String): DimWithPos {
            val split = s.split(":".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            return DimWithPos(Integer.parseInt(split[0]), BlockPos(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])))
        }
    }
}

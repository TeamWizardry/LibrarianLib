package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.features.base.block.EnumStringSerializable
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*

/**
 * @author WireSegal
 * Created at 5:32 PM on 11/17/16.
 */
enum class EnumBiFacing(val primary: EnumFacing, val secondary: EnumFacing) : EnumStringSerializable {
    UP_NORTH(UP, NORTH),
    UP_SOUTH(UP, SOUTH),
    UP_WEST(UP, WEST),
    UP_EAST(UP, EAST),
    UP_DOWN(UP, DOWN),
    DOWN_NORTH(DOWN, NORTH),
    DOWN_SOUTH(DOWN, SOUTH),
    DOWN_WEST(DOWN, WEST),
    DOWN_EAST(DOWN, EAST),
    WEST_NORTH(WEST, NORTH),
    WEST_SOUTH(WEST, SOUTH),
    WEST_EAST(WEST, EAST),
    EAST_NORTH(EAST, NORTH),
    EAST_SOUTH(EAST, SOUTH),
    NORTH_SOUTH(NORTH, SOUTH);

    operator fun contains(f: EnumFacing): Boolean {
        return primary == f || secondary == f
    }

    fun getOther(f: EnumFacing): EnumFacing {
        if (primary == f) return secondary
        return primary
    }

    companion object {
        @JvmStatic
        fun getBiForFacings(a: EnumFacing, b: EnumFacing): EnumBiFacing {
            return values().firstOrNull { it.primary == a && it.secondary == b || it.secondary == a && it.primary == b } ?:
                    throw IllegalArgumentException("Someone tried to make a bifacing out of " + a.name + " and " + b.name)
        }
    }
}

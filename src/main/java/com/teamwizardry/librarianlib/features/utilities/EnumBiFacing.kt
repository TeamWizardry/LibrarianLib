package com.teamwizardry.librarianlib.features.utilities

import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.IStringSerializable
import java.util.*

/**
 * @author WireSegal
 * Created at 5:32 PM on 11/17/16.
 */
enum class EnumBiFacing(val primary: EnumFacing, val secondary: EnumFacing) : IStringSerializable {
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

    override fun getName(): String {
        return name.toLowerCase(Locale.ROOT)
    }

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
            return values().filter { it.primary == a && it.secondary == b || it.secondary == a && it.primary == b }.firstOrNull() ?:
                    throw IllegalArgumentException("Someone tried to make a bifacing out of " + a.name + " and " + b.name)
        }
    }
}

package com.teamwizardry.librarianlib.common.util.math.interpolate.position

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.util.math.Vec3d

/**
 * Interpolate between start and end. `get(0)` equals [start], and `get(1)` equals [end]
 */
class InterpLine(val start: Vec3d, val end: Vec3d) : InterpFunction<Vec3d> {
    override fun get(i: Float): Vec3d {
        return start + (end-start) * i
    }

}
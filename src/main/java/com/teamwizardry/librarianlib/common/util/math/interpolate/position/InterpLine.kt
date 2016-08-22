package com.teamwizardry.librarianlib.common.util.math.interpolate.position

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.util.math.Vec3d

/**
 * Created by TheCodeWarrior
 */
class InterpLine(val start: Vec3d, val end: Vec3d) : InterpFunction<Vec3d> {
    override fun get(i: Float): Vec3d {
        return start + (end-start) * i
    }

}
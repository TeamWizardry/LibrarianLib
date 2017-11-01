package com.teamwizardry.librarianlib.features.animator.internal

import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.registerLerper
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.math.Vec3d

/**
 * TODO: Document file VecLerpers
 *
 * Created by TheCodeWarrior
 */
object VecLerpers {
    init {
        LerperHandler.registerLerper(Vec2d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }

        LerperHandler.registerLerper(Vec3d::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }
    }
}

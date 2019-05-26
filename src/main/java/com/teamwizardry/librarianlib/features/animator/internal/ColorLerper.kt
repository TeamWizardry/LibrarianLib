package com.teamwizardry.librarianlib.features.animator.internal

import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.registerLerper
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.util.math.Vec3d
import java.awt.Color

/**
 * TODO: Document file VecLerpers
 *
 * Created by TheCodeWarrior
 */
object ColorLerper {
    init {
        LerperHandler.registerLerper(Color::class.javaObjectType) { from, to, frac ->
            fun compute(from: Int, to: Int): Int = (to * frac + from * (1-frac)).toInt().clamp(0, 255)

            Color(
                compute(from.red, to.red),
                compute(from.green, to.green),
                compute(from.blue, to.blue),
                compute(from.alpha, to.alpha)
            )
        }
    }
}

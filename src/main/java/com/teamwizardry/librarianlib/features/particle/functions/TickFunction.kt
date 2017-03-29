package com.teamwizardry.librarianlib.features.particle.functions

import com.teamwizardry.librarianlib.features.particle.ParticleBase

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
interface TickFunction {
    fun tick(particle: ParticleBase)
}

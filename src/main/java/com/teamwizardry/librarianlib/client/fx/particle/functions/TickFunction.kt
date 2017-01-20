package com.teamwizardry.librarianlib.client.fx.particle.functions

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBase

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
interface TickFunction {
    fun tick(particle: ParticleBase)
}

package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystem
import net.minecraft.entity.LivingEntity

abstract class TestSystem: ParticleSystem() {
    abstract fun spawn(player: LivingEntity)
}
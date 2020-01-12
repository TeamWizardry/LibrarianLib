package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystem
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity

abstract class TestSystem(val id: String): ParticleSystem() {
    abstract fun spawn(player: Entity)
}
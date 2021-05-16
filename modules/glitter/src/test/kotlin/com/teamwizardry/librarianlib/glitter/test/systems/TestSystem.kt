package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import net.minecraft.entity.Entity

abstract class TestSystem: ParticleSystem() {
    abstract fun spawn(player: Entity)
}
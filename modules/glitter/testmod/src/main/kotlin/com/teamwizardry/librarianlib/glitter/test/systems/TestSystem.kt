package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

abstract class TestSystem(name: Identifier) : ParticleSystem(name) {
    abstract fun spawn(player: Entity)
}
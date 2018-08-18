package com.teamwizardry.librarianlib.features.particlesystem.bindings

import com.teamwizardry.librarianlib.features.particlesystem.ParticlePath
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

class PathPositionBinding(
        private val lifetime: ReadParticleBinding,
        private val age: ReadParticleBinding,
        private val retime: ReadParticleBinding?,
        private val offset: ReadParticleBinding?,
        private val origin: ReadParticleBinding,
        private val path: ParticlePath
): ReadParticleBinding {
    init {
        lifetime.require(1)
        age.require(1)
        retime?.require(1)
        offset?.require(1)
        origin.require(3)
    }

    override val size: Int = 3

    override fun get(particle: DoubleArray, index: Int): Double {
        var time = age[particle, 0]/lifetime[particle, 0]
        if(offset != null) time += offset[particle, 0]
        if(retime != null) time *= retime[particle, 0]
        time %= 1
        return origin[particle, index] + path.getPosition(particle, time, index)
    }
}

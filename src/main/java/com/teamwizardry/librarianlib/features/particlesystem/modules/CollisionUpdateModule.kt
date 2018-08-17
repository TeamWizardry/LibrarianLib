package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require

class CollisionUpdateModule(
        private val position: ReadParticleBinding,
        private val velocity: ReadParticleBinding,
        private val endPoint: WriteParticleBinding,
        private val impactNormal: WriteParticleBinding? = null,
        private val friction: WriteParticleBinding? = null,
        private val impactFraction: WriteParticleBinding? = null
): ParticleUpdateModule {
    init {
        position.require(3)
        velocity.require(3)
        endPoint.require(3)
        impactNormal?.require(3)
        friction?.require(3)
        impactFraction?.require(1)
    }

    override fun update(particle: DoubleArray) {
        val c = ParticleWorldCollisionHandler

        val posX = position[particle, 0]
        val posY = position[particle, 1]
        val posZ = position[particle, 2]
        val velX = velocity[particle, 0]
        val velY = velocity[particle, 1]
        val velZ = velocity[particle, 2]

        c.collide(posX, posY, posZ, velX, velY, velZ)

        val endX = posX + velX*c.collisionFraction
        val endY = posY + velY*c.collisionFraction
        val endZ = posZ + velZ*c.collisionFraction
        endPoint[particle, 0] = endX
        endPoint[particle, 1] = endY
        endPoint[particle, 2] = endZ
        impactNormal?.set(particle, 0, c.collisionNormalX)
        impactNormal?.set(particle, 1, c.collisionNormalY)
        impactNormal?.set(particle, 2, c.collisionNormalZ)
        impactFraction?.set(particle, 0, 1.0)
        if(c.collisionNormalX > 0 || c.collisionNormalY > 0 || c.collisionNormalZ > 0) {
            friction?.set(particle, 0, 1-c.collisionNormalX)
            friction?.set(particle, 1, 1-c.collisionNormalY)
            friction?.set(particle, 2, 1-c.collisionNormalZ)
        } else {
            friction?.set(particle, 0, 0.0)
            friction?.set(particle, 1, 0.0)
            friction?.set(particle, 2, 0.0)
        }
    }

}

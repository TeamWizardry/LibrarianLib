package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.require

class BasicPhysicsUpdateModule(
        private val position: ReadWriteParticleBinding,
        private val previousPosition: ReadWriteParticleBinding,
        private val velocity: ReadWriteParticleBinding,
        private val gravity: Double = 0.04,
        private val bounciness: Double = 0.2,
        private val friction: Double = 0.2,
        private val damping: Double = 0.01
): ParticleUpdateModule {
    init {
        position.require(3)
        previousPosition.require(3)
        velocity.require(3)
    }

    override fun update(particle: DoubleArray) {
        val c = ParticleWorldCollisionHandler

        var posX = position[particle, 0]
        var posY = position[particle, 1]
        var posZ = position[particle, 2]
        var velX = velocity[particle, 0]
        var velY = velocity[particle, 1]
        var velZ = velocity[particle, 2]

        velX *= 1-damping
        velY *= 1-damping
        velZ *= 1-damping

        velY -= gravity

        previousPosition[particle, 0] = posX
        previousPosition[particle, 1] = posY
        previousPosition[particle, 2] = posZ

        c.collide(posX, posY, posZ, velX, velY, velZ)

        posX += velX*c.collisionFraction
        posY += velY*c.collisionFraction
        posZ += velZ*c.collisionFraction

        velX *= 1-c.collisionNormalX*(1.0 + bounciness)
        velY *= 1-c.collisionNormalY*(1.0 + bounciness)
        velZ *= 1-c.collisionNormalZ*(1.0 + bounciness)

        if(c.collisionNormalX > 0 || c.collisionNormalY > 0 || c.collisionNormalZ > 0) {
            velX *= 1-(1-c.collisionNormalX)*friction
            velY *= 1-(1-c.collisionNormalY)*friction
            velZ *= 1-(1-c.collisionNormalZ)*friction
        }

        if(c.collisionFraction < 1.0) {
            c.collide(posX, posY, posZ, velX, velY, velZ)

            posX += velX*c.collisionFraction
            posY += velY*c.collisionFraction
            posZ += velZ*c.collisionFraction

            velX *= 1-c.collisionNormalX*(1.0 + bounciness)
            velY *= 1-c.collisionNormalY*(1.0 + bounciness)
            velZ *= 1-c.collisionNormalZ*(1.0 + bounciness)

            if(c.collisionNormalX > 0 || c.collisionNormalY > 0 || c.collisionNormalZ > 0) {
                velX *= 1-(1-c.collisionNormalX)*friction
                velY *= 1-(1-c.collisionNormalY)*friction
                velZ *= 1-(1-c.collisionNormalZ)*friction
            }
        }

        position[particle, 0] = posX
        position[particle, 1] = posY
        position[particle, 2] = posZ
        velocity[particle, 0] = velX
        velocity[particle, 1] = velY
        velocity[particle, 2] = velZ
    }

}
package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule

class BasicPhysicsUpdateModule(
        private val position: ParticleBinding,
        private val previousPosition: ParticleBinding,
        private val velocity: ParticleBinding,
        private val gravity: Double = 0.04,
        private val bounciness: Double = 0.2,
        private val friction: Double = 0.2,
        private val damping: Double = 0.01
): ParticleUpdateModule {
    override fun update(particle: DoubleArray) {
        val c = ParticleWorldCollisionHandler

        var posX = position.get(particle, 0)
        var posY = position.get(particle, 1)
        var posZ = position.get(particle, 2)
        var velX = velocity.get(particle, 0)
        var velY = velocity.get(particle, 1)
        var velZ = velocity.get(particle, 2)

        velX *= 1-damping
        velY *= 1-damping
        velZ *= 1-damping

        velY -= gravity

        previousPosition.set(particle, 0, posX)
        previousPosition.set(particle, 1, posY)
        previousPosition.set(particle, 2, posZ)

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

        position.set(particle, 0, posX)
        position.set(particle, 1, posY)
        position.set(particle, 2, posZ)
        velocity.set(particle, 0, velX)
        velocity.set(particle, 1, velY)
        velocity.set(particle, 2, velZ)
    }

}
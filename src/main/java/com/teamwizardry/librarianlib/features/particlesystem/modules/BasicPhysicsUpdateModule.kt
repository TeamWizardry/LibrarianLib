package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.*
import java.lang.Math.abs

/**
 * A basic implementation of physics interaction between particles and the world.
 *
 * This module handles essentially the entire simulation of simple particles. It:
 *
 * - Updates the previous position binding
 * - Computes collisions and their effects on velocity
 * - Advances the position based upon those collisions and velocity changes
 *
 * The collision boxes are not updated every tick, as retrieving them is among the most costly operations the collision
 * handler does. A refresh may be manually requested by calling [ParticleWorldCollisionHandler.requestRefresh]
 *
 * The process of updating each tick proceeds as follows:
 *
 * 1. Store the current position in the previous position
 * 2. Dampen the velocity
 * 3. Apply gravitational acceleration
 * 4. Collide with the world
 *     1. Ray-trace the collision point
 *     2. Advance the position to the point of collision or the end of the velocity vector if there was no collision
 *     3. Return if no collision occurred
 *     4. Reflect velocity along the collision normal axis
 *     5. Apply friction along the two axes perpendicular to the normal
 * 5. If the particle just hit something, collide again to try to use up the rest of the velocity.
 *
 * Item 5 ensures that the downward velocity added to particles resting on the ground—and the resulting 0-distance
 * collision with the ground—doesn't lock them in place. In the second iteration that downward velocity will have
 * been zeroed or negated by the first collision, allowing the particle to slide or bounce as would be expected.
 */
class BasicPhysicsUpdateModule @JvmOverloads constructor(
        /**
         * The position of the particle.
         */
        @JvmField val position: ReadWriteParticleBinding,
        /**
         * The previous position of the particle. Populated so renderers can properly position the particle between ticks.
         */
        @JvmField val previousPosition: WriteParticleBinding,
        /**
         * The velocity of the particle. If the binding is a [ReadWriteParticleBinding] the final velocity will be
         * stored in this binding.
         */
        @JvmField val velocity: ReadParticleBinding,
        /**
         * The acceleration of gravity. Positive gravity imparts a downward acceleration on the particle.
         */
        @JvmField var gravity: Double = 0.04,
        /**
         * The fraction of velocity conserved upon impact. A bounciness of 0 means the particle will completely stop
         * when impacting a surface. 1.0 means the particle will bounce back with all of the velocity it had impacting,
         * essentially negating the collision axis. However, due to inaccuracies as yet unidentified, a bounciness of
         * 1.0 doesn't result in 100% velocity preservation.
         */
        @JvmField var bounciness: Double = 0.2,
        /**
         * The friction of the particle upon impact. Every time the particle impacts (or rests upon) a block, the two
         * axes perpendicular to the side being hit will be reduced by this fraction. Friction of 0 would mean perfectly
         * slippery, no velocity lost when rubbing against an object
         */
        @JvmField var friction: Double = 0.2,
        /**
         * The damping, or "drag" of the particle. Every tick the velocity will be reduced by this fraction. Setting
         * the damping to the default, 0.01, means that the particle will reach 10% velocity in just over 10 seconds
         * (0.99^229 ≈ 0.1, log.99(0.1) ≈ 229).
         */
        @JvmField var damping: Double = 0.01
): ParticleUpdateModule {
    init {
        position.require(3)
        previousPosition.require(3)
        velocity.require(3)
    }

    private var posX: Double = 0.0
    private var posY: Double = 0.0
    private var posZ: Double = 0.0
    private var velX: Double = 0.0
    private var velY: Double = 0.0
    private var velZ: Double = 0.0

    override fun update(particle: DoubleArray) {

        posX = position[particle, 0]
        posY = position[particle, 1]
        posZ = position[particle, 2]
        velX = velocity[particle, 0]
        velY = velocity[particle, 1]
        velZ = velocity[particle, 2]

        // (1. in class docs)
        previousPosition[particle, 0] = posX
        previousPosition[particle, 1] = posY
        previousPosition[particle, 2] = posZ

        // (2. in class docs)
        dampen()

        // (3. in class docs)
        accelerate()

        // (4. in class docs)
        collide()

        // (5. in class docs)
        if(ParticleWorldCollisionHandler.collisionFraction < 1.0) {
            collide(velocityMultiplier = 1-ParticleWorldCollisionHandler.collisionFraction)
        }

        position[particle, 0] = posX
        position[particle, 1] = posY
        position[particle, 2] = posZ
        if(velocity is WriteParticleBinding) {
            velocity[particle, 0] = velX
            velocity[particle, 1] = velY
            velocity[particle, 2] = velZ
        }
    }

    private fun dampen() {
        velX *= 1-damping
        velY *= 1-damping
        velZ *= 1-damping
    }

    private fun accelerate() {
        velY -= gravity
    }

    private fun collide(velocityMultiplier: Double = 1.0) {
        // (4.1 in class docs)
        ParticleWorldCollisionHandler.collide(
                posX, posY, posZ,
                velX*velocityMultiplier, velY*velocityMultiplier, velZ*velocityMultiplier
        )

        // (4.2 in class docs)
        posX += velX*ParticleWorldCollisionHandler.collisionFraction
        posY += velY*ParticleWorldCollisionHandler.collisionFraction
        posZ += velZ*ParticleWorldCollisionHandler.collisionFraction

        // (4.3 in class docs)
        if(ParticleWorldCollisionHandler.collisionFraction >= 1.0) { return }

        val axisX = abs(ParticleWorldCollisionHandler.collisionNormalX)
        val axisY = abs(ParticleWorldCollisionHandler.collisionNormalX)
        val axisZ = abs(ParticleWorldCollisionHandler.collisionNormalX)
        // (4.4 in class docs)
        velX *= 1-axisX*(1.0 + bounciness)
        velY *= 1-axisY*(1.0 + bounciness)
        velZ *= 1-axisZ*(1.0 + bounciness)

        // (4.5 in class docs)
        velX *= 1-(1-axisX)*friction
        velY *= 1-(1-axisY)*friction
        velZ *= 1-(1-axisZ)*friction
    }

}
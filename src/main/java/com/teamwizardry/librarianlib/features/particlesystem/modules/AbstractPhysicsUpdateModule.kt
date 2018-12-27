package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ReadWriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.WriteParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.VariableBinding
import com.teamwizardry.librarianlib.features.utilities.RayHitResult
import com.teamwizardry.librarianlib.features.utilities.RayWorldCollider

/**
 * A basic implementation of physics interaction between particles and the world.
 *
 * This module handles essentially the entire simulation of simple particles. It updates the previous position binding,
 * computes collisions and their effects on velocity, and advances the position to reflect those collisions and
 * velocity changes.
 *
 * The collision boxes are not updated every tick, as retrieving them is among the most costly operations the collider
 * does. A refresh may be manually requested by calling [RayWorldCollider.requestRefresh]
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
 *     5. Apply frictionBinding along the two axes perpendicular to the normal
 * 5. If the particle just hit something, collide again to try to use up the rest of the velocity.
 *
 * Item 5 ensures that the downward velocity added to particles resting on the ground—and the resulting 0-distance
 * collision with the ground—doesn't lock them in place. In the second iteration that downward velocity will have
 * been zeroed or negated by the first collision, allowing the particle to slide or bounce as would be expected.
 */
abstract class AbstractPhysicsUpdateModule @JvmOverloads constructor(
        /**
         * The position of the particle.
         */
        @JvmField open val position: ReadWriteParticleBinding,
        /**
         * The previous position of the particle. Populated so renderers can properly position the particle between ticks.
         */
        @JvmField open val previousPosition: WriteParticleBinding,
        /**
         * The velocity of the particle. If the binding is a [ReadWriteParticleBinding] the final velocity will be
         * stored in this binding.
         *
         * Velocity is set, not added.
         */
        @JvmField open val velocity: ReadParticleBinding,
        /**
         * If enabled, will allow the particle to collide with blocks in the world
         */
        @JvmField open val enableCollisionBinding: ReadParticleBinding = ConstantBinding(0.0),
        /**
         * The acceleration of gravity. Positive gravity imparts a downward acceleration on the particle.
         *
         * Subtracted from particle's speed every tick.
         *
         * 0.04 is a good number to start with.
         */
        @JvmField open var gravityBinding: ReadParticleBinding = ConstantBinding(0.0),
        /**
         * The fraction of velocity conserved upon impact. A bouncinessBinding of 0 means the particle will completely stop
         * when impacting a surface. 1.0 means the particle will bounce back with all of the velocity it had impacting,
         * essentially negating the collision axis. However, due to inaccuracies as yet unidentified, a bouncinessBinding of
         * 1.0 doesn't result in 100% velocity preservation.
         *
         * Only useful if enableCollision is set to true.
         *
         * 0.2 is a good number to start with.
         */
        @JvmField open var bouncinessBinding: ReadParticleBinding = ConstantBinding(0.0),
        /**
         * The frictionBinding of the particle upon impact. Every time the particle impacts (or rests upon) a block, the two
         * axes perpendicular to the side being hit will be reduced by this fraction. Friction of 0 would mean perfectly
         * slippery, no velocity lost when rubbing against an object
         * Multiplies particle speed.
         *
         * Friction sliding against blocks. Only useful if enableCollision is set to true.
         *
         * 0.2 is a good number to start with.
         */
        @JvmField open var frictionBinding: ReadParticleBinding = ConstantBinding(0.0),
        /**
         * The dampingBinding, or "drag" of the particle. Every tick the velocity will be reduced by this fraction. Setting
         * the dampingBinding to 0.01 means that the particle will reach 10% velocity in just over 10 seconds
         * (0.99^229 ≈ 0.1, log.99(0.1) ≈ 229).
         * Multiplies particle speed.
         *
         * Friction in the air basically.
         *
         * 0.01 is a good number to start with.
         */
        @JvmField open var dampingBinding: ReadParticleBinding = ConstantBinding(0.0)
) : ParticleUpdateModule {

    constructor(
            position: ReadWriteParticleBinding,
            previousPosition: WriteParticleBinding,
            velocity: ReadParticleBinding,
            enableCollision: Boolean = false,
            gravity: Double = 0.0,
            bounciness: Float = 0.0f,
            friction: Float = 0.0f,
            damping: Float = 0.0f
    ) : this(
            position,
            previousPosition,
            velocity,
            ConstantBinding(if (enableCollision) 1.0 else 0.0),
            ConstantBinding(gravity),
            ConstantBinding(bounciness.toDouble()),
            ConstantBinding(friction.toDouble()),
            ConstantBinding(damping.toDouble()))

    protected var changingPos = VariableBinding(position.getSize())
    protected var changingVelocity = VariableBinding(position.getSize())
    protected val rayHit = RayHitResult()
    protected lateinit var collider: RayWorldCollider

    override fun init(particle: DoubleArray) {
        super.init(particle)

        enableCollisionBinding.load(particle)
        gravityBinding.load(particle)
        bouncinessBinding.load(particle)
        frictionBinding.load(particle)
        dampingBinding.load(particle)
    }

    override fun update(particle: DoubleArray) {
        position.load(particle)
        position.copyInto(changingPos)

        velocity.load(particle)
        velocity.copyInto(changingVelocity)

        // (1. in class docs)
        changingPos.copyInto(previousPosition)
        previousPosition.store(particle)

        // (2. in class docs)
        applyDampening()

        // (3. in class docs)
        applyGravity()

        if (enableCollisionBinding.getValue(0).toInt() != 0) {
            // (4. in class docs)
            collide()

            // (5. in class docs)
            if (rayHit.collisionFraction < 1.0) {
                collide(velocityMultiplier = 1 - rayHit.collisionFraction)
            }
        } else {
            movePos()
        }

        changingPos.copyInto(position)
        position.store(particle)

        changingVelocity.copyInto(velocity)
        if (velocity is WriteParticleBinding) {
            (velocity as WriteParticleBinding).store(particle)
        }
    }

    open fun movePos() {
        for (i in 0 until changingVelocity.getSize())
            changingPos.setValue(i, changingPos.getValue(i) + changingVelocity.getValue(i))
    }

    open fun applyDampening() {
        for (i in 0 until changingVelocity.getSize())
            changingVelocity.setValue(i, changingVelocity.getValue(i) * 1 - dampingBinding.getValue(0))
    }

    open fun applyGravity() {
        changingVelocity.setValue(1, changingVelocity.getValue(1) - gravityBinding.getValue(0))
    }

    abstract fun collide(velocityMultiplier: Double = 1.0)

}
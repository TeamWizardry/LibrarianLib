package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunctionBasic
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp
import com.teamwizardry.librarianlib.common.util.plus
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color

/**
 * Create a particle builder
 *
 * Particle builders are used to easily create particles and allow you to pass the
 * particle definition to various methods such as [ParticleSpawner.spawn]
 *
 */
class ParticleBuilder(private var lifetime: Int) {
    var animOverflow: Int = 0
        private set
    var animStart: Int = 0
        private set
    var positionFunc: InterpFunction<Vec3d>? = null
        private set
    var easingFunc: InterpFunction<Float> = InterpFunction.ONE_TO_ONE
        private set
    var scaleFunc: InterpFunction<Float> = StaticInterp(1f)
        private set
    var colorFunc: InterpFunction<Color>? = null
        private set
    var renderFunc: RenderFunction? = null
        private set
    var movementMode: EnumMovementMode = EnumMovementMode.IN_DIRECTION
        private set

    var positionOffset: Vec3d = Vec3d.ZERO
        private set
    var motion: Vec3d = Vec3d.ZERO
        private set
    var acceleration: Vec3d = Vec3d(0.0, -0.01, 0.0)
        private set
    var deceleration: Vec3d = Vec3d(0.95, 0.95, 0.95)
        private set
    var friction: Vec3d = Vec3d(0.5, 1.0, 0.5)
        private set
    var jitterMagnitude: Vec3d = Vec3d(0.05, 0.05, 0.05)
        private set
    var jitterChance: Float = 0.0f
        private set
    var motionEnabled: Boolean = false
        private set
    /**
     * Set the number of ticks the particle will live
     */
    fun setLifetime(value: Int): ParticleBuilder {
        lifetime = value
        return this
    }

    /**
     * Set the starting point of the animation (in lifetime ticks).
     *
     * Allows you to start in the middle of an animation
     */
    fun setAnimStart(value: Int): ParticleBuilder {
        animStart = value
        return this
    }

    /**
     * Set the overflow amount of the animation (in lifetime ticks).
     *
     * Allows you to have the particle die before it finishes the animation
     */
    fun setAnimOverflow(value: Int): ParticleBuilder {
        animOverflow = value
        return this
    }

    /**
     * Set the position function for the particle.
     *
     * Positions are relative to the position specified in the [build] method
     *
     * @see StaticInterp
     * @see InterpLine
     * @see InterpHelix
     * @see InterpCircle
     * @see InterpBezier3D
     * @see InterpUnion
     */
    fun setPositionFunction(value: InterpFunction<Vec3d>): ParticleBuilder {
        positionFunc = value
        return this
    }

    /**
     * An offset to add to the position passed in to [build()]
     */
    fun setPositionOffset(value: Vec3d): ParticleBuilder {
        positionOffset = value
        return this
    }

    /**
     * Sets the motion
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setMotion(value: Vec3d): ParticleBuilder {
        motion = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the motion
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addMotion(value: Vec3d): ParticleBuilder {
        motion += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the acceleration
     *
     * Each tick this value is added to the particle's motion
     *
     * (calling this method enables standard particle motion)
     */
    fun setAcceleration(value: Vec3d): ParticleBuilder {
        acceleration = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the acceleration
     *
     * Each tick this value is added to the particle's motion
     *
     * (calling this method enables standard particle motion)
     */
    fun addAcceleration(value: Vec3d): ParticleBuilder {
        acceleration += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the deceleration
     *
     * Each tick the particle's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setDeceleration(value: Vec3d): ParticleBuilder {
        deceleration = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the deceleration
     *
     * Each tick the particle's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addDeceleration(value: Vec3d): ParticleBuilder {
        deceleration += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the friction
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setFriction(value: Vec3d): ParticleBuilder {
        friction = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the friction
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addFriction(value: Vec3d): ParticleBuilder {
        friction += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the motion enabled flag
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun setMotionEnabled(value: Boolean): ParticleBuilder {
        motionEnabled = value
        return this
    }

    /**
     * Sets the motion enabled flag to true
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun enableMotion(): ParticleBuilder {
        motionEnabled = true
        return this
    }

    /**
     * Sets the motion enabled flag to false
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun disableMotion(): ParticleBuilder {
        motionEnabled = false
        return this
    }

    /**
     * Set jitter amount.
     *
     * Each tick there is a 1 in [chance] chance of `rand(-1 to 1) *` each of [value]'s components being added
     * to the particle's motion.
     */
    fun setJitter(chance: Int, value: Vec3d): ParticleBuilder {
        jitterMagnitude = value
        jitterChance = 1f / chance
        return this
    }

    /**
     * Set the scale function for the particle.
     */
    fun setScaleFunction(value: InterpFunction<Float>): ParticleBuilder {
        scaleFunc = value
        return this
    }

    /**
     * Shortcut for a static scale
     */
    fun setScale(value: Float): ParticleBuilder {
        scaleFunc = StaticInterp(value)
        return this
    }

    /**
     * Set the color function for the particle.
     *
     * @see InterpColorComponents
     * @see InterpColorHSV
     */
    fun setColor(value: InterpFunction<Color>): ParticleBuilder {
        colorFunc = value
        return this
    }

    /**
     * Shortcut for creating a static color
     */
    fun setColor(value: Color): ParticleBuilder {
        colorFunc = StaticInterp(value)
        return this
    }

    /**
     * Set the render function for the particle
     *
     * @see RenderFunctionBasic
     */
    fun setRender(value: RenderFunction): ParticleBuilder {
        renderFunc = value
        return this
    }

    /**
     * Shortcut for creating a basic render function
     */
    fun setRender(value: ResourceLocation): ParticleBuilder {
        renderFunc = RenderFunctionBasic(value)
        return this
    }

    /**
     * Set the movement mode for the particle
     *
     * @see EnumMovementMode
     */
    fun setMovementMode(value: EnumMovementMode): ParticleBuilder {
        movementMode = value
        return this
    }

    /**
     * Build an instance of the particle.
     *
     * Returns null and prints a warning if the color function or render function are null.
     */
    fun build(world: World, pos: Vec3d): ParticleBase? {
        val renderFunc_ = renderFunc

        if(renderFunc_ == null) {
            LibrarianLog.warn("Particle render function was null!!")
            return null
        }

        return ParticleBase(world, pos + positionOffset, lifetime, animStart, animOverflow,
                positionFunc ?: StaticInterp(Vec3d.ZERO), easingFunc, colorFunc ?: StaticInterp(Color.WHITE),
                renderFunc_, movementMode, scaleFunc,
                motionEnabled, motion, acceleration, deceleration, friction, jitterMagnitude, jitterChance)
    }
}

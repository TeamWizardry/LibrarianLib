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
import java.util.function.Consumer

/**
 * Create a particle builder
 *
 * Particle builders are used to easily create particles and allow you to pass the
 * particle definition to various methods such as [ParticleSpawner.spawn]
 *
 */
class ParticleBuilder(private var lifetime: Int) {
    // region Animation Start/End
    /**
     * Set the starting point of the animation (as a unit float).
     */
    fun setAnimStart(value: Float): ParticleBuilder {
        animStart = value
        return this
    }

    /**
     * Set the overflow amount of the animation (as a unit float).
     */
    fun setAnimEnd(value: Float): ParticleBuilder {
        animEnd = value
        return this
    }

    // endregion

    // region Render function and related interps

    // region Render function
    /**
     * Set the render function for the particle
     *
     * @see RenderFunctionBasic
     */
    fun setRenderFunction(value: RenderFunction): ParticleBuilder {
        renderFunc = value
        return this
    }

    /**
     * Shortcut for creating a basic render function
     */
    fun setRender(value: ResourceLocation): ParticleBuilder {
        renderFunc = RenderFunctionBasic(value, false)
        return this
    }
    // endregion

    // region Color function

    /**
     * Set the color function for the particle.
     *
     * @see InterpColorComponents
     * @see InterpColorHSV
     */
    fun setColorFunction(value: InterpFunction<Color>): ParticleBuilder {
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

    // endregion

    // region Alpha function
    /**
     * Set the alpha function for the particle.
     */
    fun setAlphaFunction(value: InterpFunction<Float>): ParticleBuilder {
        alphaFunc = value
        return this
    }

    /**
     * Shortcut for creating a static color
     */
    fun setAlpha(value: Float): ParticleBuilder {
        alphaFunc = StaticInterp(value)
        return this
    }

    // region shortcuts

    // TODO Add alpha functions and alpha function shortcuts

    // endregion

    // endregion

    // region Scale function
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
    // endregion

    // endregion

    // region Position function

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
        positionEnabled = true
        positionFunc = value
        return this
    }

    // region Position enabled
    /**
     * Disable the position function
     */
    fun disablePosition(): ParticleBuilder {
        positionEnabled = false
        return this
    }

    /**
     * Enable the position function
     */
    fun enablePosition(): ParticleBuilder {
        positionEnabled = true
        return this
    }

    /**
     * Set the position function enabled flag
     */
    fun setPositionEnabled(value: Boolean): ParticleBuilder {
        positionEnabled = value
        return this
    }
    // endregion

    // region Movement Mode
    /**
     * Set the movement mode for the particle
     *
     * @see EnumMovementMode
     */
    fun setMovementMode(value: EnumMovementMode): ParticleBuilder {
        movementMode = value
        return this
    }
    // endregion

    // endregion

    // region Motion stuff

    // region Motion
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
    // endregion

    // region Modifiers
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
    // endregion

    // region Motion enabled
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
    // endregion

    // endregion

    // region Plain ol' position stuff

    // region Position offset

    /**
     * An offset to add to the position passed in to [build()]
     */
    fun setPositionOffset(value: Vec3d): ParticleBuilder {
        positionOffset = value
        return this
    }

    /**
     * An offset to add to the position passed in to [build()]
     */
    fun addPositionOffset(value: Vec3d): ParticleBuilder {
        positionOffset += value
        return this
    }

    // endregion

    // endregion

    // region Randomization

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
     * Clear the randomization lambdas
     */
    fun clearRandomizationLambdas(): ParticleBuilder {
        randomizationLambdas.clear()
        return this
    }

    /**
     * Add a randomization lambda
     */
    fun addRandomizationLambda(r: Consumer<ParticleBuilder>): ParticleBuilder {
        randomizationLambdas.add(r)
        return this
    }

    /**
     * Add a randomization lambda
     */
    fun addRandomizationLambda(r: (ParticleBuilder) -> Unit): ParticleBuilder {
        randomizationLambdas.add(Consumer<ParticleBuilder>(r))
        return this
    }

    // endregion

    // anim start/end
    var animStart: Float = 0f
        private set
    var animEnd: Float = 1f
        private set

    // render stuff
    var renderFunc: RenderFunction? = null
        private set
    var colorFunc: InterpFunction<Color>? = null
        private set
    var alphaFunc: InterpFunction<Float> = StaticInterp(1f)
        private set
    var scaleFunc: InterpFunction<Float> = StaticInterp(1f)
        private set

    // pos func
    var positionEnabled: Boolean = false
        private set
    var positionFunc: InterpFunction<Vec3d>? = null
        private set
    var movementMode: EnumMovementMode = EnumMovementMode.IN_DIRECTION
        private set

    // motion stuff
    var motionEnabled: Boolean = false
        private set
    var motion: Vec3d = Vec3d.ZERO
        private set
    var acceleration: Vec3d = Vec3d(0.0, -0.01, 0.0)
        private set
    var deceleration: Vec3d = Vec3d(0.95, 0.95, 0.95)
        private set
    var friction: Vec3d = Vec3d(0.9, 1.0, 0.9)
        private set

    // plain ol' position
    var positionOffset: Vec3d = Vec3d.ZERO
        private set
    var canCollide: Boolean = true
        private set

    // randomization
    var jitterMagnitude: Vec3d = Vec3d(0.05, 0.05, 0.05)
        private set
    var jitterChance: Float = 0.0f
        private set
    var randomizationLambdas: MutableList<Consumer<ParticleBuilder>> = mutableListOf()

    /**
     * Set the number of ticks the particle will live
     */
    fun setLifetime(value: Int): ParticleBuilder {
        lifetime = value
        return this
    }

    /**
     * Build an instance of the particle.
     *
     * Returns null and prints a warning if the color function or render function are null.
     */
    fun build(world: World, pos: Vec3d): ParticleBase? {
        randomizationLambdas.forEach { it.accept(this) }

        val renderFunc_ = renderFunc

        if(renderFunc_ == null) {
            LibrarianLog.warn("Particle render function was null!!")
            return null
        }

        return ParticleBase(world, pos + positionOffset, lifetime, animStart, animEnd,
                positionFunc ?: StaticInterp(Vec3d.ZERO), colorFunc ?: StaticInterp(Color.WHITE), alphaFunc,
                renderFunc_, movementMode, scaleFunc,
                motionEnabled, positionEnabled, canCollide, motion, acceleration, deceleration, friction, jitterMagnitude, jitterChance)
    }

    /**
     * Clones this builder.
     */
    fun clone() : ParticleBuilder {
        val v = ParticleBuilder(lifetime)

        cloneTo(v)
        
        return v
    }

    /**
     * Copies all the values from this builder to the other builder.
     *
     * All properies point to the same objects (not a deep copy) except for the randomizationLambdas list. The list in
     * the other object is cleared and the lambdas from this object are copied in. Modifying one list won't modify the
     * other one.
     */
    fun cloneTo(v: ParticleBuilder) {
        v.animStart = this.animStart
        v.animEnd = this.animEnd

        // render stuff
        v.renderFunc = this.renderFunc
        v.colorFunc = this.colorFunc
        v.alphaFunc = this.alphaFunc
        v.scaleFunc = this.scaleFunc

        // pos func
        v.positionEnabled = this.positionEnabled
        v.positionFunc = this.positionFunc
        v.movementMode = this.movementMode

        // motion stuff
        v.motionEnabled = this.motionEnabled
        v.motion = this.motion
        v.acceleration = this.acceleration
        v.deceleration = this.deceleration
        v.friction = this.friction

        // plain ol' position
        v.positionOffset = this.positionOffset
        v.canCollide = this.canCollide

        // randomization
        v.jitterMagnitude = this.jitterMagnitude
        v.jitterChance = this.jitterChance

        v.randomizationLambdas.clear()
        v.randomizationLambdas.addAll(this.randomizationLambdas)
    }
}

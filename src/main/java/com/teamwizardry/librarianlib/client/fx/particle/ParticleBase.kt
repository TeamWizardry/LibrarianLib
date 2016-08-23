package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.plus
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color
import java.util.*
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.*
import com.teamwizardry.librarianlib.client.fx.particle.functions.*
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class ParticleBase(
        val world: World,
        val position: Vec3d,
        val lifetime: Int,
        val animStart: Int,
        val animOverflow: Int,
        val positionFunc: InterpFunction<Vec3d>,
        val easing: InterpFunction<Float>,
        val colorFunc: InterpFunction<Color>,
        val renderFunc: RenderFunction,
        val movementMode: EnumMovementMode,
        val scaleFunc: InterpFunction<Float>
) : Particle(world, 0.0, 0.0, 0.0) {

    private var lastPos: Vec3d = positionFunc.get(0f)
    private val randomNum: Int = ThreadLocalRandom.current().nextInt()

    init {
        particleMaxAge = lifetime
        setPosition(lastPos + position)
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        val sprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite("wizardry:particles/sparkle")
        this.setParticleTexture(sprite)
    }

    private fun setPosition(vec: Vec3d) {
        setPosition(vec.xCoord, vec.yCoord, vec.zCoord)
    }

    override fun getFXLayer(): Int {
        return 1
    }

    override fun onUpdate() {
        particleAge++
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        val i = ( particleAge.toFloat() + animStart) / ( particleMaxAge.toFloat() + animOverflow - animStart )

        if(particleAge > particleMaxAge) {
            this.setExpired()
        }

        val pos = positionFunc.get(Math.min(1f, easing.get(i)))

        if(movementMode == EnumMovementMode.PHASE) {
            setPosition(pos + position)
        } else {
            val direction: Vec3d
            if(movementMode == EnumMovementMode.IN_DIRECTION) {
                direction = pos - lastPos
            } else { // effectivly `else if(movementMode == EnumMovementMode.TOWARD_POINT)`, only else to avoid errors
                direction = pos - Vec3d(posX, posY, posZ)
            }
            this.motionX = direction.xCoord
            this.motionY = direction.yCoord
            this.motionZ = direction.zCoord
            this.moveEntity(this.motionX, this.motionY, this.motionZ)
        }

        lastPos = pos
    }

    override fun renderParticle(worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float) {
//        super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ)
        val i = Math.min(1f, ( particleAge.toFloat() + partialTicks ) / particleMaxAge.toFloat())

        val posX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks.toDouble() - Particle.interpPosX
        val posY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks.toDouble() - Particle.interpPosY
        val posZ = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks.toDouble() - Particle.interpPosZ

        val brightness = this.getBrightnessForRender(partialTicks)
        val skyLight = brightness shr 16 and 65535
        val blockLight = brightness and 65535

        renderFunc.render(i, this, colorFunc.get(i), worldRendererIn, entityIn, partialTicks,
                rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ,
                scaleFunc.get(i), posX, posY, posZ, skyLight, blockLight)
    }

    override fun isTransparent(): Boolean {
        return true
    }
}

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
    fun setPosition(value: InterpFunction<Vec3d>): ParticleBuilder {
        positionFunc = value
        return this
    }

    /**
     * Set the scale function for the particle.
     */
    fun setScale(value: InterpFunction<Float>): ParticleBuilder {
        scaleFunc = value
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
     * Set the render function for the particle
     *
     * @see RenderFunctionBasic
     */
    fun setRender(value: RenderFunction): ParticleBuilder {
        renderFunc = value
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
     * Returns null and prints a warning if the position function, color function, or render function are null.
     */
    fun build(world: World, pos: Vec3d): ParticleBase? {
        val positionFunc_ = positionFunc
        val colorFunc_ = colorFunc
        val renderFunc_ = renderFunc

        if(positionFunc_ == null || colorFunc_ == null || renderFunc_ == null) {
            var nullVars: String = ""
            if(positionFunc_ == null) nullVars += "pos,"
            if(colorFunc_ == null) nullVars += "color,"
            if(renderFunc_ == null) nullVars += "render,"

            LibrarianLog.warn("Particle functions were null: [$nullVars]")
            return null
        }

        return ParticleBase(world, pos, lifetime, animStart, animOverflow, positionFunc_, easingFunc, colorFunc_, renderFunc_, movementMode, scaleFunc)
    }
}

enum class EnumMovementMode {
    /**
     * Particles don't collide, they follow their path exactly and phase through walls
     */
    PHASE,
    /**
     * Particles always try to move toward the point specified by the position function, but will collide with blocks.
     *
     * If a particle's position function is a straight line and it goes through an angled wall, the particle will slide
     * along the wall until it passes the edge, then it will quickly go toward the point it was supposed to be at
     *
     * ```
     * _- == particle path
     * // == wall
     *     __
     * ___-//-___
     * ```
     */
    TOWARD_POINT,
    /**
     * Particles will go the direction specified by the previous and current function values and will collide with blocks.
     *
     * If a particle's position function is a straight line and it goes through an angled wall, the particle will slide
     * along the wall until it passes the edge, then continue to go the direction the position function is moving at
     * that point in time. This may mean the particle doesn't go the distance projected in the position function as it
     * may slow down while it's hitting an object.
     *
     * ```
     * _- == particle path
     * // == wall
     * .. == actual position function location
     * ** == length difference from time spent slowly sliding along wall
     *       ________******
     * ____-//.............
     * ```
     */
    IN_DIRECTION
}
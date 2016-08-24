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
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.util.ResourceLocation
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
open class ParticleBase(
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
        val scaleFunc: InterpFunction<Float>,
        val motionEnabled: Boolean,
        var motion: Vec3d,
        val acceleration: Vec3d,
        val deceleration: Vec3d,
        val friction: Vec3d,
        val jitterMagnitude: Vec3d = Vec3d(0.05, 0.05, 0.05),
        val jitterChance: Float = 0.1f
) : Particle(world, 0.0, 0.0, 0.0) {

    open fun tickFirst() {}
    open fun tickLast() {}

    val pos: Vec3d
        get() {
            return Vec3d(posX, posY, posZ)
        }

    private var lastPos: Vec3d = positionFunc.get(0f)
    private var jitterMotion: Vec3d = Vec3d.ZERO

    private val randomNum: Int = ThreadLocalRandom.current().nextInt()

    internal var depthSquared: Double = 0.0

    init {
        particleMaxAge = lifetime
        setPosition(lastPos + position)
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        this.particleAlpha = 0.5f // just so minecraft renders the particle on the translucent layer
    }

    private fun setPosition(vec: Vec3d) {
        setPosition(vec.xCoord, vec.yCoord, vec.zCoord)
    }

    override fun getFXLayer(): Int {
        return 1
    }


    override fun onUpdate() {
        tickFirst()
        particleAge++
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        val i = ( particleAge.toFloat() + animStart) / ( particleMaxAge.toFloat() + animOverflow - animStart )

        if(particleAge > particleMaxAge) {
            this.setExpired()
        }

        var pos = (
                if(motionEnabled)
                    Vec3d.ZERO
                else
                    positionFunc.get(Math.min(1f, easing.get(i)))
                )
        if(!motionEnabled)
            pos += jitterMotion

        if(ThreadLocalRandom.current().nextFloat() < jitterChance) {
            var jitter = jitterMagnitude * Vec3d(
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0
            )
            if(motionEnabled)
                motion += jitter
            else
                jitterMotion += jitter
        }

        if(movementMode == EnumMovementMode.PHASE) {
            setPosition(pos + position + motion)
        } else {
            var direction: Vec3d
            if(movementMode == EnumMovementMode.IN_DIRECTION) {
                direction = pos - lastPos
            } else { // effectivly `else if(movementMode == EnumMovementMode.TOWARD_POINT)`, only else to avoid errors
                direction = pos - ( Vec3d(posX, posY, posZ) - position )
            }
            direction += motion
            this.motionX = direction.xCoord
            this.motionY = direction.yCoord
            this.motionZ = direction.zCoord
            this.moveEntity(this.motionX, this.motionY, this.motionZ)
        }

        if(motionEnabled) {
            motion += acceleration
            motion *= deceleration
            if(this.isCollided)
                motion *= friction
        }

        lastPos = pos

        tickLast()
    }

    override fun renderParticle(worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float) {

    }

    fun renderActual(worldRendererIn: VertexBuffer, info: ParticleRenderInfo) {

        val i = Math.min(1f, ( particleAge.toFloat() + info.partialTicks ) / particleMaxAge.toFloat())

        val posX = this.prevPosX + (this.posX - this.prevPosX) * info.partialTicks.toDouble() - Particle.interpPosX
        val posY = this.prevPosY + (this.posY - this.prevPosY) * info.partialTicks.toDouble() - Particle.interpPosY
        val posZ = this.prevPosZ + (this.posZ - this.prevPosZ) * info.partialTicks.toDouble() - Particle.interpPosZ

        val brightness = this.getBrightnessForRender(info.partialTicks)
        val skyLight = brightness shr 16 and 65535
        val blockLight = brightness and 65535

        renderFunc.render(i, this, colorFunc.get(i), worldRendererIn, info.entityIn, info.partialTicks,
                info.rotationX, info.rotationZ, info.rotationYZ, info.rotationXY, info.rotationXZ,
                scaleFunc.get(i), posX, posY, posZ, skyLight, blockLight)
    }

    override fun isTransparent(): Boolean {
        return true
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
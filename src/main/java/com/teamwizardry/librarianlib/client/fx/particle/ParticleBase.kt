package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp
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
import com.teamwizardry.librarianlib.common.util.*
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
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
) {

    open fun tickFirst() {}
    open fun tickLast() {}

    val radius: Vec3d = Vec3d(0.1, 0.1, 0.1)

    var pos: Vec3d = position
    var prevPos: Vec3d = pos
    var velocity: Vec3d = motion
    var age: Int = 0
    var isCollided: Boolean = false
    var entityBoundingBox: AxisAlignedBB = createBB(pos - radius, pos + radius)

    private var lastPos: Vec3d = positionFunc.get(0f)
    private var jitterMotion: Vec3d = Vec3d.ZERO

    private val randomNum: Int = ThreadLocalRandom.current().nextInt()

    internal var depthSquared: Double = 0.0

    init {
        setPosition(lastPos + position)
    }

    fun onUpdate() {
        tickFirst()
        age++
        prevPos = pos

        val i = ( age.toFloat() + animStart) / ( lifetime.toFloat() + animOverflow - animStart )

        if(age > lifetime) {
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
                direction = pos - ( this.pos - position )
            }
            direction += motion
            this.velocity = direction
            this.moveEntity()
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

    fun render(worldRendererIn: VertexBuffer, info: ParticleRenderInfo) {

        val i = Math.min(1f, ( age.toFloat() + info.partialTicks ) / lifetime.toFloat())

        val pos = this.prevPos + (this.pos - prevPos) * info.partialTicks.toDouble() - Vec3d(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ)

        val brightness = this.getBrightnessForRender(info.partialTicks)
        val skyLight = brightness shr 16 and 65535
        val blockLight = brightness and 65535

        renderFunc.render(i, this, colorFunc.get(i), worldRendererIn, info.entityIn, info.partialTicks,
                info.rotationX, info.rotationZ, info.rotationYZ, info.rotationXY, info.rotationXZ,
                scaleFunc.get(i), pos, skyLight, blockLight)
    }

    /*
    COPIED FROM MINECRAFT'S PARTICLE CLASS:
     */

    private var isExpiredStore: Boolean = false
    protected var canCollide: Boolean = true

    fun setExpired() {
        this.isExpiredStore = true
    }

    fun setPosition(p: Vec3d) {
        this.pos = p
        this.entityBoundingBox = AxisAlignedBB(p - radius, p + radius)
    }

    fun moveEntity() {
        var x = this.velocity.xCoord
        var y = this.velocity.yCoord
        var z = this.velocity.zCoord
        val d0 = y

        if (this.canCollide) {
            val list = this.world.getCollisionBoxes(null, this.entityBoundingBox.addCoord(x, y, z))

            for (axisalignedbb in list) {
                y = axisalignedbb.calculateYOffset(this.entityBoundingBox, y)
            }

            this.entityBoundingBox = this.entityBoundingBox.offset(0.0, y, 0.0)

            for (axisalignedbb1 in list) {
                x = axisalignedbb1.calculateXOffset(this.entityBoundingBox, x)
            }

            this.entityBoundingBox = this.entityBoundingBox.offset(x, 0.0, 0.0)

            for (axisalignedbb2 in list) {
                z = axisalignedbb2.calculateZOffset(this.entityBoundingBox, z)
            }

            this.entityBoundingBox = this.entityBoundingBox.offset(0.0, 0.0, z)
        } else {
            this.entityBoundingBox = this.entityBoundingBox.offset(x, y, z)
        }

        this.resetPositionToBB()

        if (x != x) {
            this.isCollided = true
            this.velocity = this.velocity.withX(0)
        }

        if (y != y) {
            this.isCollided = true
            this.velocity = this.velocity.withY(0)
        }

        if (z != z) {
            this.isCollided = true
            this.velocity = this.velocity.withZ(0)
        }
    }

    protected fun resetPositionToBB() {
        val axisalignedbb = this.entityBoundingBox
        this.pos = Vec3d(
                (axisalignedbb.minX + axisalignedbb.maxX) / 2.0,
                (axisalignedbb.minY + axisalignedbb.maxY) / 2.0,
                (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0
        )
    }

    fun getBrightnessForRender(p_189214_1_: Float): Int {
        val blockpos = BlockPos(this.pos.xCoord, this.pos.yCoord, this.pos.zCoord)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }

    /**
     * Returns true if this effect has not yet expired. "I feel happy! I feel happy!"
     */
    fun isAlive(): Boolean {
        return !this.isExpiredStore
    }

    fun createBB(min: Vec3d, max: Vec3d): AxisAlignedBB {
        return AxisAlignedBB(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord)
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
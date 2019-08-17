package com.teamwizardry.librarianlib.features.particle

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.features.particle.functions.TickFunction
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
open class ParticleBase internal constructor(
        val world: World,
        val position: Vec3d,
        val lifetime: Int,
        val animStart: Float,
        val animEnd: Float,
        val positionFunc: InterpFunction<Vec3d>,
        val colorFunc: InterpFunction<Color>,
        val alphaFunc: InterpFunction<Float>,
        val renderFunc: RenderFunction,
        val tickFunc: TickFunction?,
        val movementMode: EnumMovementMode,
        val scaleFunc: InterpFunction<Float>,
        val rotationFunc: InterpFunction<Float>,
        val motionEnabled: Boolean,
        val positionEnabled: Boolean,
        var canCollide: Boolean,
        initialMotion: Vec3d,
        var acceleration: Vec3d,
        var deceleration: Vec3d,
        var friction: Vec3d,
        var jitterMagnitude: Vec3d = vec(0.05, 0.05, 0.05),
        var jitterChance: Float = 0.1f,
        var canBounce: Boolean = false,
        var bounceMagnitude: Double = 0.9
) {

    open fun tickFirst() {
        // NO-OP
    }

    open fun tickLast() {
        // NO-OP
    }

    val radius: Vec3d = vec(0.1, 0.1, 0.1)

    var pos: Vec3d = position
        set(value) {
            field = value
            this.entityBoundingBox = AxisAlignedBB(field - radius, field + radius)
        }
    var prevPos: Vec3d = pos
    var velocity: Vec3d = initialMotion
    var age: Int = 0
    var isCollided: Boolean = false
    var entityBoundingBox: AxisAlignedBB = createBB(pos - radius, pos + radius)

    private var lastInterp: Vec3d = positionFunc.get(animStart)
    private var lastInterpMotion: Vec3d = Vec3d.ZERO

    private var jitterMotion: Vec3d = Vec3d.ZERO

    private val randomNum: Int = ThreadLocalRandom.current().nextInt()

    internal var depthSquared: Double = 0.0

    init {
        pos = position + (if (positionEnabled) lastInterp else Vec3d.ZERO)
        prevPos = pos
    }

    fun animPos(): Float {
        return animStart + (animEnd - animStart) * (age.toFloat() / lifetime.toFloat())
    }

    fun onUpdate() {
        tickFirst()

        prevPos = pos

        if (++age > lifetime) {
            this.setExpired()
        }

        val interpPos = positionFunc.get(Math.max(Math.min(animPos(), 1f), 0f))
        var jitter = Vec3d.ZERO

        if (ThreadLocalRandom.current().nextDouble() < jitterChance)
            jitter = jitterMagnitude * vec(
                    ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0
            )

        if (positionEnabled) {
            jitterMotion += jitter

            if (movementMode == EnumMovementMode.TOWARD_POINT)
                velocity = interpPos + position - pos
            if (movementMode == EnumMovementMode.IN_DIRECTION) {
                val interpMotion = interpPos - lastInterp
                velocity += interpMotion - lastInterpMotion
                lastInterpMotion = interpMotion
            }
        }
        lastInterp = interpPos

        this.moveEntity()

        if (motionEnabled) {
            velocity += jitter
            velocity += acceleration
            velocity *= deceleration
            if (this.isCollided)
                velocity *= friction

        }

        tickFunc?.tick(this)

        tickLast()
    }

    fun render(worldRendererIn: BufferBuilder, info: ParticleRenderInfo) {

        val i = animPos()

        val pos = this.prevPos + (this.pos - prevPos) * info.partialTicks.toDouble() - vec(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ)

        val brightness = this.getBrightnessForRender()
        val skyLight = brightness shr 16 and 65535
        val blockLight = brightness and 65535

        renderFunc.render(i, this, colorFunc.get(i), alphaFunc.get(i), worldRendererIn, info.entityIn, info.partialTicks,
                info.rotationX, info.rotationZ, info.rotationYZ, info.rotationXY, info.rotationXZ,
                scaleFunc.get(i), rotationFunc.get(i), pos, skyLight, blockLight)
    }

    /*
    COPIED FROM MINECRAFT'S PARTICLE CLASS:
     */

    private var isExpiredStore: Boolean = false

    fun setExpired() {
        this.isExpiredStore = true
    }

    fun moveEntity() {
        var x = this.velocity.x
        var y = this.velocity.y
        var z = this.velocity.z

        if (this.canCollide) {
            val list = this.world.getCollisionBoxes(null, this.entityBoundingBox.expand(x, y, z))

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
        this.isCollided = false

        if (x != velocity.x) {
            this.isCollided = true

            if (canBounce) this.velocity = this.velocity.withX(this.velocity.x * -bounceMagnitude)
            else this.velocity = this.velocity.withX(0)
        }

        if (y != velocity.y) {
            this.isCollided = true

            if (canBounce) this.velocity = this.velocity.withY(this.velocity.y * -bounceMagnitude)
            else this.velocity = this.velocity.withY(0)
        }

        if (z != velocity.z) {
            this.isCollided = true

            if (canBounce) this.velocity = this.velocity.withZ(this.velocity.z * -bounceMagnitude)
            else this.velocity = this.velocity.withZ(0)
        }
    }

    protected fun resetPositionToBB() {
        val axisalignedbb = this.entityBoundingBox
        this.pos = vec(
                (axisalignedbb.minX + axisalignedbb.maxX) / 2.0,
                (axisalignedbb.minY + axisalignedbb.maxY) / 2.0,
                (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0
        )
    }

    fun getBrightnessForRender(): Int {
        val blockpos = BlockPos(this.pos.x, this.pos.y, this.pos.z)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }

    /**
     * Returns true if this effect has not yet expired. "I feel happy! I feel happy!"
     */
    fun isAlive(): Boolean {
        return !this.isExpiredStore
    }

    fun createBB(min: Vec3d, max: Vec3d): AxisAlignedBB {
        return AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z)
    }
}

enum class EnumMovementMode {
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

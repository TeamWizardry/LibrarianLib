package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp
import com.teamwizardry.librarianlib.common.util.minus
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ParticleBase(
        val world: World,
        val lifetime: Int,
        val animTime: Int,
        val positionFunc: InterpFunction<Vec3d>,
        val easing: InterpFunction<Float>,
        val colorFunc: InterpFunction<Color>,
        val renderFunc: RenderFunction,
        val movementMode: EnumMovementMode,
        val scaleFunc: InterpFunction<Float>
) : Particle(world, 0.0, 0.0, 0.0) {

    private var lastPos: Vec3d = positionFunc.get(0f)

    init {
        particleMaxAge = lifetime
        setPosition(lastPos)
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
        var i = particleAge.toFloat() / particleMaxAge.toFloat()

        if(particleAge > particleMaxAge) {
            this.setExpired()
        }

        var pos = positionFunc.get(Math.min(1f, easing.get(i)))

        if(movementMode == EnumMovementMode.PHASE) {
            setPosition(pos)
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
        var i = Math.min(1f, ( particleAge.toFloat() + partialTicks ) / particleMaxAge.toFloat())

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

class ParticleBuilder(private val world: World, private var lifetime: Int) {
    private var animTime: Int = lifetime
    private var positionFunc: InterpFunction<Vec3d>? = null
    private var easingFunc: InterpFunction<Float> = InterpFunction.ONE_TO_ONE
    private var scaleFunc: InterpFunction<Float> = StaticInterp(1f)
    private var colorFunc: InterpFunction<Color>? = null
    private var renderFunc: RenderFunction? = null
    private var movementMode: EnumMovementMode = EnumMovementMode.IN_DIRECTION

    fun setLifetime(value: Int): ParticleBuilder {
        lifetime = value
        animTime = value
        return this
    }

    fun setPosition(value: InterpFunction<Vec3d>): ParticleBuilder {
        positionFunc = value
        return this
    }

    fun setScale(value: InterpFunction<Float>): ParticleBuilder {
        scaleFunc = value
        return this
    }

    fun setColor(value: InterpFunction<Color>): ParticleBuilder {
        colorFunc = value
        return this
    }

    fun setRender(value: RenderFunction): ParticleBuilder {
        renderFunc = value
        return this
    }

    fun setMovementMode(value: EnumMovementMode): ParticleBuilder {
        movementMode = value
        return this
    }

    fun build(): ParticleBase? {
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

        return ParticleBase(world, lifetime, animTime, positionFunc_, easingFunc, colorFunc_, renderFunc_, movementMode, scaleFunc)
    }
}

enum class EnumMovementMode { PHASE, TOWARD_POINT, IN_DIRECTION }
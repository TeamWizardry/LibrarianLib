package com.teamwizardry.librarianlib.etcetera.testmod

import net.minecraft.client.particle.IAnimatedSprite
import net.minecraft.client.particle.IParticleFactory
import net.minecraft.client.particle.IParticleRenderType
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.SpriteTexturedParticle
import net.minecraft.particles.BasicParticleType
import net.minecraft.world.World
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

object Particles {
    val TARGET_RED: BasicParticleType = BasicParticleType(true).also {
        it.setRegistryName("librarianlib-etcetera-test:target_red")
    }
    val TARGET_BLUE: BasicParticleType = BasicParticleType(true).also {
        it.setRegistryName("librarianlib-etcetera-test:target_blue")
    }
}

@OnlyIn(Dist.CLIENT)
class HitParticle private constructor(world: World, x: Double, y: Double, z: Double): SpriteTexturedParticle(world, x, y, z, 0.0, 0.0, 0.0) {
    init {
        maxAge = 1
        canCollide = false
    }

    override fun getRenderType(): IParticleRenderType {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE
    }

    override fun getScale(p_217561_1_: Float): Float {
        return 0.5f
    }

    override fun tick() {
        prevPosX = posX
        prevPosY = posY
        prevPosZ = posZ
        if (age++ >= maxAge) {
            setExpired()
        }
    }

    @OnlyIn(Dist.CLIENT)
    class Factory(private val spriteSet: IAnimatedSprite): IParticleFactory<BasicParticleType> {
        override fun makeParticle(typeIn: BasicParticleType, worldIn: World, x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double): Particle? {
            val hitParticle = HitParticle(worldIn, x, y, z)
            hitParticle.selectSpriteRandomly(spriteSet)
            return hitParticle
        }
    }

}
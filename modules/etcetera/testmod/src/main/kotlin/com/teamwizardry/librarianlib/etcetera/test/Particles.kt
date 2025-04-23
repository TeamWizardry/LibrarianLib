package com.teamwizardry.librarianlib.etcetera.test

import com.teamwizardry.librarianlib.testcore.platform.TestCoreClientPlatform
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType

internal object Particles {
    val TARGET_RED: SimpleParticleType = object : SimpleParticleType(true) {}
    val TARGET_BLUE: SimpleParticleType = object : SimpleParticleType(true) {}

    fun register() {
        LibLibEtceteraTest.registrars.particleType.register(LibLibEtceteraTest.createId("target_red")) { TARGET_RED }
        LibLibEtceteraTest.registrars.particleType.register(LibLibEtceteraTest.createId("target_blue")) { TARGET_BLUE }
    }

    fun registerClient() {
        TestCoreClientPlatform.instance.registerParticleFactory(TARGET_RED, HitParticle::Factory)
        TestCoreClientPlatform.instance.registerParticleFactory(TARGET_BLUE, HitParticle::Factory)
    }
}

class HitParticle(world: ClientWorld, x: Double, y: Double, z: Double, vx: Double, vy: Double, vz: Double) :
    SpriteBillboardParticle(world, x, y, z, vx, vy, vz) {
    init {
        maxAge = 1
        collidesWithWorld = false
        scale = 0.5f
    }

    override fun tick() {
        prevPosX = x
        prevPosY = y
        prevPosZ = z
        if (age++ >= maxAge) {
            markDead()
        }
    }

    override fun getType(): ParticleTextureSheet {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE
    }

    class Factory(private val spriteSet: SpriteProvider) : ParticleFactory<SimpleParticleType> {
        override fun createParticle(
            typeIn: SimpleParticleType,
            worldIn: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            vx: Double,
            vy: Double,
            vz: Double
        ): Particle {
            val hitParticle = HitParticle(worldIn, x, y, z, vx, vy, vz)
            hitParticle.setSprite(spriteSet)
            return hitParticle
        }
    }
}

package com.teamwizardry.librarianlib.etcetera.test

import com.teamwizardry.librarianlib.core.util.Client
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.client.particle.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.SimpleParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry

object Particles {
    val TARGET_RED: SimpleParticleType = FabricParticleTypes.simple(true)
    val TARGET_BLUE: SimpleParticleType = FabricParticleTypes.simple(true)

    fun register() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("liblib-etcetera-test:target_red"), TARGET_RED)
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of("liblib-etcetera-test:target_blue"), TARGET_BLUE)
    }

    fun registerClient() {
        ParticleFactoryRegistry.getInstance().register(TARGET_RED) { spriteProvider ->
            HitParticle.Factory(spriteProvider)
        }
        ParticleFactoryRegistry.getInstance().register(TARGET_BLUE) { spriteProvider ->
            HitParticle.Factory(spriteProvider)
        }
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

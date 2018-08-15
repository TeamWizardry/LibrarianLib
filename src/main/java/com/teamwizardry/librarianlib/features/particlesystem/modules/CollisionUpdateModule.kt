package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.features.particlesystem.ParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class CollisionUpdateModule(
        private val position: ParticleBinding,
        private val velocity: ParticleBinding,
        private val endPoint: ParticleBinding,
        private val impactNormal: ParticleBinding? = null,
        private val friction: ParticleBinding? = null,
        private val impactFraction: ParticleBinding? = null
): ParticleUpdateModule {
    override fun update(particle: DoubleArray) {
        val c = ParticleWorldCollisionHandler

        val posX = position.get(particle, 0)
        val posY = position.get(particle, 1)
        val posZ = position.get(particle, 2)
        val velX = velocity.get(particle, 0)
        val velY = velocity.get(particle, 1)
        val velZ = velocity.get(particle, 2)

        c.collide(posX, posY, posZ, velX, velY, velZ)

        val endX = posX + velX*c.collisionFraction
        val endY = posY + velY*c.collisionFraction
        val endZ = posZ + velZ*c.collisionFraction
        endPoint.set(particle, 0, endX)
        endPoint.set(particle, 1, endY)
        endPoint.set(particle, 2, endZ)
        impactNormal?.set(particle, 0, c.collisionNormalX)
        impactNormal?.set(particle, 1, c.collisionNormalY)
        impactNormal?.set(particle, 2, c.collisionNormalZ)
        impactFraction?.set(particle, 0, 1.0)
        if(c.collisionNormalX > 0 || c.collisionNormalY > 0 || c.collisionNormalZ > 0) {
            friction?.set(particle, 0, 1-c.collisionNormalX)
            friction?.set(particle, 1, 1-c.collisionNormalY)
            friction?.set(particle, 2, 1-c.collisionNormalZ)
        } else {
            friction?.set(particle, 0, 0.0)
            friction?.set(particle, 1, 0.0)
            friction?.set(particle, 2, 0.0)
        }
    }

}

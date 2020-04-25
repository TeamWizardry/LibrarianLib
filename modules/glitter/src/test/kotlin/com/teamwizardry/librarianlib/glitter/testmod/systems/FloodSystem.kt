package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.MathHelper



object FloodSystem: TestSystem("flood") {
    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
            position = position,
            previousPosition = previousPosition,
            velocity = velocity,
            enableCollision = true,
            gravity = 0.02,
            bounciness = 0.8f,
            friction = 0.02f,
            damping = 0.01f
        ))

        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = ResourceLocation("minecraft", "textures/item/clay_ball.png")
            ),
            previousPosition = previousPosition,
            position = position,
            color = color,
            size = ConstantBinding(0.2)
        ))
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val spread = 45

        repeat(20) {
            doSpawn(
                eyePos,
                player.rotationPitch + (Math.random() - 0.5).toFloat() * spread,
                player.rotationYaw + (Math.random() - 0.5).toFloat() * spread
            )
        }
    }

    fun doSpawn(pos: Vec3d, pitch: Float, yaw: Float) {
        val look = getVectorForRotation(pitch, yaw)

        val spawnDistance = 2
        val spawnVelocity = 1.0

        this.addParticle(200,
            // position
            pos.x + look.x * spawnDistance,
            pos.y + look.y * spawnDistance,
            pos.z + look.z * spawnDistance,
            // previous position
            pos.x + look.x * spawnDistance,
            pos.y + look.y * spawnDistance,
            pos.z + look.z * spawnDistance,
            // velocity
            look.x * spawnVelocity,
            look.y * spawnVelocity,
            look.z * spawnVelocity,
            // color
            Math.random(),
            Math.random(),
            Math.random(),
            1.0
        )
    }

    fun getVectorForRotation(pitch: Float, yaw: Float): Vec3d {
        val f = pitch * (Math.PI.toFloat() / 180f)
        val f1 = -yaw * (Math.PI.toFloat() / 180f)
        val f2 = MathHelper.cos(f1)
        val f3 = MathHelper.sin(f1)
        val f4 = MathHelper.cos(f)
        val f5 = MathHelper.sin(f)
        return Vec3d((f3 * f4).toDouble(), (-f5).toDouble(), (f2 * f4).toDouble())
    }
}
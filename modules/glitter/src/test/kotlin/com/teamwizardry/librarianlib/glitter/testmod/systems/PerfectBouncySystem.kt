package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.testmod.modules.VelocityRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object PerfectBouncySystem: TestSystem("perfect_bouncy") {
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
            gravity = ConstantBinding(0.02),
            bounciness = ConstantBinding(1.0),
            friction = ConstantBinding(0.00),
            damping = ConstantBinding(0.0)
        ))

        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = ResourceLocation("minecraft", "textures/item/clay_ball.png")
            ),
            previousPosition = position,
            position = position,
            color = color,
            size = ConstantBinding(0.05)
        ))
        renderModules.add(VelocityRenderModule(
            blend = true,
            previousPosition = position,
            position = position,
            velocity = velocity,
            color = color,
            size = 1f,
            alpha = null
        ))
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

        val spawnDistance = 2
        val spawnVelocity = 0.2

        this.addParticle(200,
            // position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
            // previous position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
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
}
package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.test.modules.VelocityRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object PerfectBouncySystem : TestSystem() {
    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)

        updateModules.add(
            BasicPhysicsUpdateModule(
                position = position,
                previousPosition = previousPosition,
                velocity = velocity,
                enableCollision = true,
                gravity = ConstantBinding(0.02),
                bounciness = ConstantBinding(1.0),
                friction = ConstantBinding(0.00),
                damping = ConstantBinding(0.0)
            )
        )

        renderModules.add(
            SpriteRenderModule.build(
                Identifier("minecraft", "textures/item/clay_ball.png"),
                position,
            )
                .previousPosition(previousPosition)
                .color(color)
                .size(0.15)
                .build()
        )
        renderModules.add(
            VelocityRenderModule(
                blend = true,
                previousPosition = position,
                position = position,
                velocity = velocity,
                color = color,
                size = 1f,
                alpha = null
            )
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getCameraPosVec(1f)
        val look = player.rotationVector

        val spawnDistance = 2
        val spawnVelocity = 0.2

        this.addParticle(
            200,
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
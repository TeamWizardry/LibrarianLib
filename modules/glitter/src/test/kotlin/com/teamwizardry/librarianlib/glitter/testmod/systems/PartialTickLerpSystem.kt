package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object PartialTickLerpSystem : TestSystem("partial_tick_lerp") {
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
                gravity = ConstantBinding(0.0),
                bounciness = ConstantBinding(0.0),
                friction = ConstantBinding(0.0),
                damping = ConstantBinding(0.0)
            )
        )

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderModule.simpleRenderType(loc("minecraft", "textures/item/clay_ball.png")),
                position,
            )
                .previousPosition(previousPosition)
                .color(color)
                .size(0.2)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

        val spawnDistance = 2
        val spawnVelocity = 1

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
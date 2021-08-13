package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderOptions
import com.teamwizardry.librarianlib.glitter.test.modules.VelocityRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object UpFacingVectorSystem : TestSystem(Identifier("liblib-glitter-test:up_facing_vector")) {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val upVector = bind(3)
        val facingVector = bind(3)

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderOptions.build(Identifier("ll-glitter-test:textures/glitter/arrow.png"))
                    .diffuseLight(true)
                    .cull(false)
                    .build(),
                pos,
            )
                .color(color)
                .size(0.15, 0.3)
                .upVector(upVector)
                .facingVector(facingVector)
                .build()
        )

        renderModules.add(
            VelocityRenderModule(
                blend = true,
                previousPosition = pos,
                position = pos,
                velocity = upVector,
                color = ConstantBinding(1.0, 0.0, 0.0, 1.0),
                size = 2f,
                alpha = null,
                scale = 0.25
            )
        )

        renderModules.add(
            VelocityRenderModule(
                blend = true,
                previousPosition = pos,
                position = pos,
                velocity = facingVector,
                color = ConstantBinding(0.0, 0.0, 1.0, 1.0),
                size = 2f,
                alpha = null,
                scale = 0.1
            )
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getCameraPosVec(1f)
        val look = player.rotationVector

        this.addParticle(
            200,
            eyePos.x + look.x * 2,
            eyePos.y + look.y * 2,
            eyePos.z + look.z * 2,
            Math.random(),
            Math.random(),
            Math.random(),
            1.0,
            Math.random() * 2 - 1,
            Math.random() * 2 - 1,
            Math.random() * 2 - 1,
            Math.random() * 2 - 1,
            Math.random() * 2 - 1,
            Math.random() * 2 - 1,
        )
    }
}
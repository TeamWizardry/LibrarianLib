package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.testmod.modules.VelocityRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object UpVectorSystem : TestSystem("up_vector") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val upVector = bind(3)

        renderModules.add(
            SpriteRenderModule.build(
                loc("librarianlib-glitter-test:textures/glitter/arrow.png"),
                pos,
            )
                .color(color)
                .size(0.15, 0.3)
                .upVector(upVector)
                .build()
        )

        renderModules.add(
            VelocityRenderModule(
                blend = true,
                previousPosition = pos,
                position = pos,
                velocity = upVector,
                color = ConstantBinding(1.0, 0.0, 1.0, 1.0),
                size = 2f,
                alpha = null
            )
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

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
        )
    }
}
package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object SizeAxesSystem: TestSystem(Identifier("liblib-glitter-test:size_axes")) {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val size = bind(2)

        renderModules.add(
            SpriteRenderModule.build(
                Identifier("minecraft", "textures/item/clay_ball.png"),
                pos,
            )
                .color(color)
                .size(size)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getCameraPosVec(1f)
        val look = player.rotationVector

        this.addParticle(200,
            eyePos.x + look.x * 2,
            eyePos.y + look.y * 2,
            eyePos.z + look.z * 2,
            Math.random(),
            Math.random(),
            Math.random(),
            1.0,
            Math.random() * 0.3,
            Math.random() * 0.3,
        )
    }
}
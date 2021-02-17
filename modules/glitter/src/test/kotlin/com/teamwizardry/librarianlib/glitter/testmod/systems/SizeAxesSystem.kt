package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity

object SizeAxesSystem: TestSystem("size_axes") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val size = bind(2)

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderModule.simpleRenderType(loc("minecraft", "textures/item/clay_ball.png")),
                pos,
            )
                .color(color)
                .size(size)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

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
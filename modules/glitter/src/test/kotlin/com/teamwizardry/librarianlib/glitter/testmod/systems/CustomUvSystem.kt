package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity

object CustomUvSystem : TestSystem("custom_uv") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val uvSize = bind(2)
        val uvOffset = bind(2)

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderModule.simpleRenderType(loc("minecraft", "textures/block/orange_glazed_terracotta.png")),
                pos,
            )
                .color(color)
                .size(0.25)
                .uvSize(uvSize)
                .uvOffset(uvOffset)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

        this.addParticle(
            200,
            eyePos.x + look.x * 2, // x
            eyePos.y + look.y * 2, // y
            eyePos.z + look.z * 2, // z
            1.0, 1.0, 1.0, 1.0, //Math.random(), Math.random(), Math.random(), 1.0, // color

            Math.random() * 0.3, Math.random() * 0.3, // uv size
            Math.random() * 0.7, Math.random() * 0.7, // uv offset
        )
    }
}
package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object SpriteSheetSystem : TestSystem() {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val sprite = bind(1)

        renderModules.add(
            SpriteRenderModule.build(
                Identifier("ll-glitter-test:textures/glitter/spritesheet.png"),
                pos
            )
                .previousPosition(pos)
                .color(color)
                .size(0.2)
                .spriteSheet(2, sprite)
                .build()
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
            Math.random(),
            Math.random() * 4
        )
    }
}
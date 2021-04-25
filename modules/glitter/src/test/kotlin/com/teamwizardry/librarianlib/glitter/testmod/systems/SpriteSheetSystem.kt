package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object SpriteSheetSystem : TestSystem("spritesheet") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val sprite = bind(1)

        renderModules.add(
            SpriteRenderModule.build(
                loc("ll-glitter-test:textures/glitter/spritesheet.png"),
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
            Math.random(),
            Math.random() * 4
        )
    }
}
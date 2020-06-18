package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object SpriteSheetSystem: TestSystem("spritesheet") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val sprite = bind(1)

        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = ResourceLocation("librarianlib-glitter-test:textures/glitter/spritesheet.png")
            ),
            previousPosition = pos,
            position = pos,
            color = color,
            size = ConstantBinding(0.2),
            spriteSheetSize = 2,
            spriteIndex = sprite
        ))
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
            Math.random(),
            Math.random() * 4
        )
    }
}
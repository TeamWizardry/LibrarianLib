package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystem
import com.teamwizardry.librarianlib.particles.bindings.ConstantBinding
import com.teamwizardry.librarianlib.particles.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d

object SpriteSheetSystem: TestSystem("spritesheet") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val sprite = bind(1)

        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = ResourceLocation("librarianlib-particles-test:textures/particles/spritesheet.png")
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
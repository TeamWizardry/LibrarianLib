package com.teamwizardry.librarianlib.glitter.testmod.systems

import com.teamwizardry.librarianlib.math.plus
import com.teamwizardry.librarianlib.math.times
import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import com.teamwizardry.librarianlib.glitter.modules.DepthSortModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

object DepthSortSystem: TestSystem("depthsort") {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val depth = bind(1)

        globalUpdateModules.add(DepthSortModule(pos, depth))
        renderModules.add(SpriteRenderModule(
            renderType = SpriteRenderModule.simpleRenderType(
                sprite = ResourceLocation("librarianlib-glitter-test:textures/glitter/depthsort.png")
            ),
            previousPosition = pos,
            position = pos,
            color = color,
            size = ConstantBinding(1.0)
        ))
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec
        val center = eyePos + look * 5

        for(i in 0 until 5) {
            this.addParticle(2000,
                center.x + (Math.random() * 2 - 0.5) * 3,
                center.y + (Math.random() * 2 - 0.5) * 3,
                center.z + (Math.random() * 2 - 0.5) * 3,
                Math.random(),
                Math.random(),
                Math.random(),
                1.0
            )
        }
    }
}
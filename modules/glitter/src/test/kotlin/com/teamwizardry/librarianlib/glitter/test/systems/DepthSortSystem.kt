package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.modules.DepthSortModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.math.plus
import com.teamwizardry.librarianlib.math.times
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object DepthSortSystem : TestSystem(Identifier("liblib-glitter-test:depth_sort")) {
    override fun configure() {
        val pos = bind(3)
        val color = bind(4)
        val depth = bind(1)

        globalUpdateModules.add(DepthSortModule(pos, depth))
        renderModules.add(
            SpriteRenderModule.build(Identifier("ll-glitter-test:textures/glitter/depthsort.png"), pos)
                .previousPosition(pos)
                .color(color)
                .size(1.0)
                .build()
        )
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getCameraPosVec(1f)
        val look = player.rotationVector
        val center = eyePos + look * 5

        for (i in 0 until 5) {
            this.addParticle(
                2000,
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
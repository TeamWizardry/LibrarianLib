package com.teamwizardry.librarianlib.glitter.test.systems

import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderOptions
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

object DiffuseLitSystem : TestSystem() {
    override fun configure() {
        val pos = bind(3)

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderOptions.build(Identifier("minecraft", "textures/item/snowball.png"))
                    .diffuseLight(true)
                    .build(),
                pos,
            )
                .size(0.2)
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
        )
    }
}
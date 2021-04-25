package com.teamwizardry.librarianlib.foundation.testmod.customtypes.client

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestEntity
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.Identifier

class TestEntityRenderer(renderManager: EntityRendererManager): EntityRenderer<TestEntity>(renderManager) {
    override fun getEntityTexture(entity: TestEntity): Identifier {
        return loc("minecraft:block/dirt.png")
    }
}
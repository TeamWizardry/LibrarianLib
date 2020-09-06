package com.teamwizardry.librarianlib.foundation.testmod.customtypes.client

import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestEntity
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.util.ResourceLocation

class TestEntityRenderer(renderManager: EntityRendererManager): EntityRenderer<TestEntity>(renderManager) {
    override fun getEntityTexture(entity: TestEntity): ResourceLocation {
        return loc("minecraft:block/dirt.png")
    }
}
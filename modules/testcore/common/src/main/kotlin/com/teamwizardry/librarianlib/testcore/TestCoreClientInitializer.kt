package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.content.impl.TestEntityRenderer
import com.teamwizardry.librarianlib.testcore.module.TestModuleClient
import com.teamwizardry.librarianlib.testcore.resources.RuntimeResources
import com.teamwizardry.librarianlib.testcore.resources.TestBlockModelGenerator
import com.teamwizardry.librarianlib.testcore.resources.TestItemModelGenerator
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers

public object TestCoreClientInitializer {
    private val logger = TestCoreMod.logManager.makeLogger<TestCoreClientInitializer>()

    public fun onInitialize() {
        for (moduleClient in TestModuleClient.instances) {
            moduleClient.initializeClient(TestModContentManager.getOrCreateModule(moduleClient.moduleId))
        }

        for(entityConfig in TestModContentManager.entities.values) {
            EntityRendererRegistry.register({ entityConfig.entityTypeInstance }) { dispatcher ->
                TestEntityRenderer(dispatcher)
            }
        }

        for(itemConfig in TestModContentManager.items.values) {
            RuntimeResources.addAsset(TestItemModelGenerator.generateModel(itemConfig.id))
            RuntimeResources.addAsset(TestItemModelGenerator.generateModelDef(itemConfig.id))
        }

        for(blockConfig in TestModContentManager.blocks.values) {
            RuntimeResources.addAsset(TestBlockModelGenerator.generateBlockStates(blockConfig))
            RuntimeResources.addAsset(TestBlockModelGenerator.generateItemModel(blockConfig))
            RenderTypeRegistry.register(RenderLayer.getCutout(), blockConfig.blockInstance)
        }

        logger.info("Generated {} assets", RuntimeResources.resources.size)
    }
}
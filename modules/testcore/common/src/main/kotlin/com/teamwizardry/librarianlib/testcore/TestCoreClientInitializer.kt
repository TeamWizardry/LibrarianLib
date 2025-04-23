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
            moduleClient.initializeClient(TestModContentManager.getOrCreateModule(moduleClient.module))
        }

        for(moduleConfig in TestModContentManager.modules.values) {
            RuntimeResources.addTranslation(moduleConfig.itemGroup.translationKey, moduleConfig.itemGroup.name)
        }

        for(entityConfig in TestModContentManager.entities.values) {
            EntityRendererRegistry.register({ entityConfig.entityTypeInstance }) { dispatcher ->
                TestEntityRenderer(dispatcher)
            }
            RuntimeResources.addTranslation(entityConfig.entityTypeInstance.translationKey, entityConfig.name)
        }

        for(itemConfig in TestModContentManager.items.values) {
            RuntimeResources.addAsset(TestItemModelGenerator.generateModel(itemConfig.id))
            RuntimeResources.addAsset(TestItemModelGenerator.generateModelDef(itemConfig.id))
            RuntimeResources.addTranslation(itemConfig.instance.translationKey, itemConfig.name)
        }

        for(blockConfig in TestModContentManager.blocks.values) {
            RuntimeResources.addAsset(TestBlockModelGenerator.generateBlockStates(blockConfig))
            RuntimeResources.addAsset(TestBlockModelGenerator.generateItemModel(blockConfig))
            RenderTypeRegistry.register(RenderLayer.getCutout(), blockConfig.blockInstance)
            RuntimeResources.addTranslation(blockConfig.blockInstance.translationKey, blockConfig.name)
        }

        logger.info("Generated {} assets", RuntimeResources.resources.size)
    }
}
package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.content.impl.TestEntityRenderer
import com.teamwizardry.librarianlib.testcore.module.TestModuleClient
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import net.minecraft.data.client.BlockStateVariant
import net.minecraft.data.client.BlockStateVariantMap
import net.minecraft.data.client.VariantSettings
import net.minecraft.data.client.VariantsBlockStateSupplier
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

public object TestCoreClientInitializer {
    public fun onInitialize() {
        for (moduleClient in TestModuleClient.instances) {
            moduleClient.initializeClient(TestModContentManager.getOrCreateModule(moduleClient.moduleId))
        }

        for(entityConfig in TestModContentManager.entities.values) {
            EntityRendererRegistry.register({ entityConfig.entityTypeInstance }) { dispatcher ->
                TestEntityRenderer(dispatcher)
            }
        }
//        logger.info("Performing client registration")
//        for(config in objects.values) {
//            logger.info("Registering ${config.id}")
//            config.registerClient(resources)
//        }
//        resources.writeLang()
//        RRPCallback.BEFORE_VANILLA.register {
//            it.add(resources.runtimeResourcePack)
//        }
    }

    private fun registerItems() {
//        val testModel = TestItemModel(id)
//        resources.runtimeResourcePack.addModel(Identifier.of(id.namespace, "item/${id.path}"), testModel.model)
//        ColorProviderRegistry.ITEM.register(testModel.colorProvider, instance)
    }

    private fun registerBlocks() {
//        val model = Identifier.of("liblib-testcore:block/test_block/${blockInstance.modelName}")
//
//        val state = VariantsBlockStateSupplier.create(blockInstance, BlockStateVariant.create().put(VariantSettings.MODEL, model))
//        if(directional) {
//            state.coordinate(
//                BlockStateVariantMap.create(Properties.FACING)
//                    .register(Direction.DOWN, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
//                    .register(Direction.UP, BlockStateVariant.create())
//                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
//                    .register(
//                        Direction.SOUTH,
//                        BlockStateVariant.create()
//                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R180)
//                    )
//                    .register(
//                        Direction.WEST,
//                        BlockStateVariant.create()
//                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R270)
//                    )
//                    .register(
//                        Direction.EAST,
//                        BlockStateVariant.create()
//                            .put(VariantSettings.X, VariantSettings.Rotation.R90)
//                            .put(VariantSettings.Y, VariantSettings.Rotation.R90)
//                    )
//            )
//        }
//        resources.runtimeResourcePack.addBlockState(Identifier.of(id.namespace, "blockstates/${id.path}"), state)
//
//        resources.runtimeResourcePack.addModel(
//            Identifier.of(id.namespace, "item/${id.path}"),
//            ModelJsonBuilder.create("$model")
//        )
    }

    private fun registerEntities() {


//        resources.lang.add(this.type, name)
    }
}
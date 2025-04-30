package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.content.UnitTestSuite
import com.teamwizardry.librarianlib.testcore.junit.UnitTestCommand
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon
import dev.architectury.registry.registries.RegistrarManager

public object TestCoreCommonInitializer {
    private val logger = TestCoreMod.logManager.makeLogger<TestCoreCommonInitializer>()

    public val manager: RegistrarManager by lazy { RegistrarManager.get(TestCoreMod.MODID) }

    public fun onInitialize() {
        manager.builder<UnitTestSuite>(UnitTestSuite.REGISTRY_KEY.value).build()

        for (moduleCommon in TestModuleCommon.instances) {
            moduleCommon.initializeCommon(TestModContentManager.getOrCreateModule(moduleCommon.module))
        }
        TestModContentManager.locked = true

        for(itemGroupConfig in TestModContentManager.itemGroups.values) {
            itemGroupConfig.module.registrars.itemGroup.register(itemGroupConfig.id) { itemGroupConfig.instance }
        }

        for(itemConfig in TestModContentManager.items.values) {
            itemConfig.module.registrars.item.register(itemConfig.id) { itemConfig.instance }
        }

        for(blockConfig in TestModContentManager.blocks.values) {
            blockConfig.blockEntityType?.also { blockEntityType ->
                blockConfig.module.registrars.blockEntityType.register(blockConfig.id) { blockEntityType }
            }
            blockConfig.module.registrars.block.register(blockConfig.id) { blockConfig.blockInstance }
            blockConfig.module.registrars.item.register(blockConfig.id) { blockConfig.itemInstance }
        }

        for(entityConfig in TestModContentManager.entities.values) {
            entityConfig.module.registrars.entityType.register(entityConfig.id) { entityConfig.entityTypeInstance }
        }

        for (unitTestConfig in TestModContentManager.unitTests.values) {
            unitTestConfig.module.registrars.unitTest.register(unitTestConfig.id) { unitTestConfig }
        }

        UnitTestCommand.register()
    }
}
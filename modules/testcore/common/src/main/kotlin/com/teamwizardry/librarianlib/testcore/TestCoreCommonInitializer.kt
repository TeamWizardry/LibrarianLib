package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.junit.UnitTestCommand
import com.teamwizardry.librarianlib.testcore.module.TestModuleCommon

public object TestCoreCommonInitializer {
    private val logger = TestCoreMod.logManager.makeLogger<TestCoreCommonInitializer>()

    public fun onInitialize() {
        for (moduleCommon in TestModuleCommon.instances) {
            moduleCommon.initializeCommon(TestModContentManager.getOrCreateModule(moduleCommon.moduleId))
        }

        for(itemGroupConfig in TestModContentManager.itemGroups.values) {
            Registrars.ITEM_GROUP.register(itemGroupConfig.id) { itemGroupConfig.instance }
        }

        for(itemConfig in TestModContentManager.items.values) {
            Registrars.ITEM.register(itemConfig.id) { itemConfig.instance }
        }

        for(blockConfig in TestModContentManager.blocks.values) {
            blockConfig.blockEntityType?.also { blockEntityType ->
                Registrars.BLOCK_ENTITY_TYPE.register(blockConfig.id) { blockEntityType }
            }
            Registrars.BLOCK.register(blockConfig.id) { blockConfig.blockInstance }
            Registrars.ITEM.register(blockConfig.id) { blockConfig.itemInstance }
        }

        for(entityConfig in TestModContentManager.entities.values) {
            Registrars.ENTITY_TYPE.register(entityConfig.id) { entityConfig.entityTypeInstance }
        }

        for (unitTestConfig in TestModContentManager.unitTests.values) {
            Registrars.UNIT_TEST.register(unitTestConfig.id) { unitTestConfig }
        }

        UnitTestCommand.register()
    }
}
package com.teamwizardry.librarianlib.testcore.content

import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.module.TestModule
import net.minecraft.util.Identifier

public class TestModuleConfig(public val module: TestModule) {
    public val itemGroup: TestItemGroupConfig =
        TestModContentManager.getOrCreateItemGroup(this, createId("item_group"))

    public fun item(name: String, config: TestItemConfig.() -> Unit = {}): TestItemConfig =
        item(createId(name), config)

    public fun block(name: String, config: TestBlockConfig.() -> Unit = {}): TestBlockConfig =
        block(createId(name), config)

    public fun entity(name: String, config: TestEntityConfig.() -> Unit = {}): TestEntityConfig =
        entity(createId(name), config)

    public fun unitTest(name: String, config: UnitTestSuite.() -> Unit = {}): UnitTestSuite =
        unitTest(createId(name), config)

    internal fun unitTest(id: Identifier, config: UnitTestSuite.() -> Unit = {}): UnitTestSuite =
        TestModContentManager.getOrCreateUnitTest(this, id).apply(config)

    internal fun item(id: Identifier, config: TestItemConfig.() -> Unit = {}): TestItemConfig =
        TestModContentManager.getOrCreateItem(this, id).apply(config)

    internal fun block(id: Identifier, config: TestBlockConfig.() -> Unit = {}): TestBlockConfig =
        TestModContentManager.getOrCreateBlock(this, id).apply(config)

    internal fun entity(id: Identifier, config: TestEntityConfig.() -> Unit = {}): TestEntityConfig =
        TestModContentManager.getOrCreateEntity(this, id).apply(config)

    public fun createId(name: String): Identifier = Identifier.of(module.modId, name)
}
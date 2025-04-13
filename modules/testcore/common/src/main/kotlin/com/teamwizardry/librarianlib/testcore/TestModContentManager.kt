package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.content.*
import com.teamwizardry.librarianlib.testcore.content.UnitTestSuite
import net.minecraft.util.Identifier

@PublishedApi
internal object TestModContentManager {
    val modules = mutableMapOf<String, TestModuleConfig>()
    val itemGroups = mutableMapOf<Identifier, TestItemGroupConfig>()
    val items = mutableMapOf<Identifier, TestItemConfig>()
    val blocks = mutableMapOf<Identifier, TestBlockConfig>()
    val entities = mutableMapOf<Identifier, TestEntityConfig>()
    val unitTests = mutableMapOf<Identifier, UnitTestSuite>()

    var locked = false

    fun getOrCreateModule(moduleId: String) =
        getOrCreate(modules, moduleId) { TestModuleConfig(moduleId) }

    fun getOrCreateItemGroup(module: TestModuleConfig, id: Identifier) =
        getOrCreate(itemGroups, id) { TestItemGroupConfig(module, id) }

    fun getOrCreateItem(module: TestModuleConfig, id: Identifier) =
        getOrCreate(items, id) { TestItemConfig(module, id) }

    fun getOrCreateBlock(module: TestModuleConfig, id: Identifier) =
        getOrCreate(blocks, id) { TestBlockConfig(module, id) }

    fun getOrCreateEntity(module: TestModuleConfig, id: Identifier) =
        getOrCreate(entities, id) { TestEntityConfig(module, id) }

    fun getOrCreateUnitTest(module: TestModuleConfig, id: Identifier): UnitTestSuite =
        getOrCreate(unitTests, id) { UnitTestSuite(module, id) }

    private fun <K : Any, V : Any> getOrCreate(map: MutableMap<K, V>, key: K, fn: () -> V): V {
        if (locked && key !in map)
            throw IllegalStateException("Items/blocks/etc. must be created in the common initializer")
        return map.getOrPut(key, fn)
    }
}
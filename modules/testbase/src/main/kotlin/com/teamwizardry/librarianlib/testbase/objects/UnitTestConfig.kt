package com.teamwizardry.librarianlib.testbase.objects

import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import org.apache.logging.log4j.Logger

@TestObjectDslMarker
class UnitTestConfig(val id: String, val name: String, group: ItemGroup, val logger: Logger): TestConfig() {
    constructor(id: String, name: String, group: ItemGroup, logger: Logger, block: UnitTestConfig.() -> Unit): this(id, name, group, logger) {
        this.block()
    }

    /**
     * The properties of the test item. Do not mutate this after this configuration has been passed to the [TestItem]
     * constructor.
     */
    val properties: Item.Properties = Item.Properties()
        .group(group)
        .maxStackSize(1)

    /**
     * The test classes to run
     */
    val tests = mutableSetOf<Class<*>>()

    /**
     * Execute the passed block with this object as the receiver. Useful for using this object as a DSL
     */
    inline operator fun invoke(crossinline block: UnitTestConfig.() -> Unit): UnitTestConfig {
        this.block()
        return this
    }

    fun addTests(vararg testClass: Class<*>): UnitTestConfig {
        this.tests.addAll(testClass)
        return this
    }

    inline fun<reified T> add(): UnitTestConfig {
        this.addTests(T::class.java)
        return this
    }
}

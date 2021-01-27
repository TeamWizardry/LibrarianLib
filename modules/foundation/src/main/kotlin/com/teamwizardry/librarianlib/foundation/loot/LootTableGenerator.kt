package com.teamwizardry.librarianlib.foundation.loot

import net.minecraft.util.ResourceLocation
import net.minecraft.loot.LootParameterSet
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import java.lang.IllegalArgumentException
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

public open class LootTableGenerator(public val parameterSet: LootParameterSet) {
    private val tables = mutableMapOf<ResourceLocation, LootTable.Builder>()

    public open fun generateTables() {}

    public fun addLootTable(name: ResourceLocation, table: LootTable.Builder) {
        if (name in tables)
            throw IllegalArgumentException("Duplicate loot table name $name")
        tables[name] = table
    }

    public fun addLootTable(name: ResourceLocation, vararg pools: LootPool.Builder) {
        addLootTable(name, createTable(*pools))
    }

    public fun createTable(vararg pools: LootPool.Builder): LootTable.Builder {
        val table = LootTable.builder()
        for(pool in pools) {
            table.addLootPool(pool)
        }
        return table
    }

    /**
     * The supplier used by the `LootTableProvider`. This is stupid, but I'll explain. This is a supplier that returns
     * the "generator" (the `Consumer`). The `LootTableProvider` then feeds that "generator" a `BiConsumer` that it can
     * call to register loot tables.
     *
     * All this instead of just returning a `Map` with the loot tables in it.
     */
    @get:JvmSynthetic
    internal val lootTableProviderSupplier: Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> =
        Supplier {
            if(tables.isEmpty())
                generateTables()
            Consumer {
                for ((name, table) in tables) {
                    it.accept(name, table)
                }
            }
        }
}
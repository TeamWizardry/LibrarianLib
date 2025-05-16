package com.teamwizardry.librarianlib.testcore.content

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier

@TestConfigDslMarker
public class TestItemGroupConfig(moduleConfig: TestModuleConfig, id: Identifier) : TestConfig(moduleConfig, id) {
    init {
        this.name = moduleConfig.module.name
    }
    public val translationKey: String = id.toTranslationKey("itemGroup")

    internal lateinit var registrySupplier: RegistrySupplier<ItemGroup>

    internal fun createInstance(): ItemGroup {
        return CreativeTabRegistry.create(Text.translatable(translationKey)) { ItemStack(Items.STICK) }
    }
}
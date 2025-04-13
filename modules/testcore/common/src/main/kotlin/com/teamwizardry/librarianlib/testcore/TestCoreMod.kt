package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.TestCoreMod.MODID
import com.teamwizardry.librarianlib.testcore.content.UnitTestSuite
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier


public object TestCoreMod {
    public const val MODID: String = "liblib-testcore"
    public val logManager: ModLogManager = ModLogManager(MODID, "LibrarianLib: Test Core")
}

internal object Registrars {
    val MANAGER by lazy { RegistrarManager.get(MODID) }
    val ITEM by lazy { MANAGER.get(RegistryKeys.ITEM) }
    val ITEM_GROUP by lazy { MANAGER.get(RegistryKeys.ITEM_GROUP) }
    val BLOCK by lazy { MANAGER.get(RegistryKeys.BLOCK) }
    val BLOCK_ENTITY_TYPE by lazy { MANAGER.get(RegistryKeys.BLOCK_ENTITY_TYPE) }
    val ENTITY_TYPE by lazy { MANAGER.get(RegistryKeys.ENTITY_TYPE) }

    val UNIT_TEST_KEY = RegistryKey.ofRegistry<UnitTestSuite>(Identifier.of("testcore:unit_tests"))
    val UNIT_TEST by lazy { MANAGER.builder<UnitTestSuite>(UNIT_TEST_KEY.value).build() }
}
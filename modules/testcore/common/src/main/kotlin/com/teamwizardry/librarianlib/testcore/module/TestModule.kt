package com.teamwizardry.librarianlib.testcore.module

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.testcore.content.UnitTestSuite
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.particle.ParticleType
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

public abstract class TestModule(public val moduleId: String, public val name: String) {
    public val modId: String = "liblib-$moduleId-test"
    public val logManager: ModLogManager = ModLogManager(modId, name)
    public val registrars: ModuleRegistrars = ModuleRegistrars(modId)

    public fun createId(name: String): Identifier = Identifier.of(modId, name)
}

public class ModuleRegistrars(private val modId: String) {
    public val manager: RegistrarManager by lazy { RegistrarManager.get(modId) }
    public val item: Registrar<Item> by lazy { manager.get(RegistryKeys.ITEM) }
    public val itemGroup: Registrar<ItemGroup> by lazy { manager.get(RegistryKeys.ITEM_GROUP) }
    public val block: Registrar<Block> by lazy { manager.get(RegistryKeys.BLOCK) }
    public val blockEntityType: Registrar<BlockEntityType<*>> by lazy { manager.get(RegistryKeys.BLOCK_ENTITY_TYPE) }
    public val entityType: Registrar<EntityType<*>> by lazy { manager.get(RegistryKeys.ENTITY_TYPE) }

    public val particleType: Registrar<ParticleType<*>> by lazy { manager.get(RegistryKeys.PARTICLE_TYPE) }

    public val unitTest: Registrar<UnitTestSuite> by lazy { manager.get(UnitTestSuite.REGISTRY_KEY) }
}

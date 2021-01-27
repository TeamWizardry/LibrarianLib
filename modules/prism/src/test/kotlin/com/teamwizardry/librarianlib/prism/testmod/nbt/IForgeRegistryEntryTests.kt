package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.prism.nbt.IForgeRegistryEntrySerializerFactory
import com.teamwizardry.librarianlib.prism.testmod.LibrarianLibPrismTestMod
import com.teamwizardry.librarianlib.testbase.LibrarianLibTestBaseModule
import com.teamwizardry.librarianlib.testbase.objects.UnitTestSuite
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class IForgeRegistryEntryTests: NBTPrismTest() {
    @Test
    fun `read+write with a mod item should be symmetrical`() {
        simple<Item, IForgeRegistryEntrySerializerFactory.IForgeRegistryEntrySerializer>(
            LibrarianLibTestBaseModule.testTool,
            NBTBuilder.string("librarianlib-testbase:test_tool")
        )
    }

    @Test
    fun `read+write with a vanilla item should be symmetrical`() {
        simple<Item, IForgeRegistryEntrySerializerFactory.IForgeRegistryEntrySerializer>(
            Items.STONE,
            NBTBuilder.string("minecraft:stone")
        )
    }

    @Test
    fun `read+write with a custom registry should be symmetrical`() {
        simple<UnitTestSuite, IForgeRegistryEntrySerializerFactory.IForgeRegistryEntrySerializer>(
            LibrarianLibPrismTestMod.primitiveTests,
            NBTBuilder.string("prism:nbt_primitives")
        )
    }

    @Test
    fun `read+write with a vanilla block should be symmetrical`() {
        simple<Block, IForgeRegistryEntrySerializerFactory.IForgeRegistryEntrySerializer>(
            Blocks.STONE,
            NBTBuilder.string("minecraft:stone")
        )
    }

    @Test
    fun `read+write with a vanilla entity type should be symmetrical`() {
        simple<EntityType<*>, IForgeRegistryEntrySerializerFactory.IForgeRegistryEntrySerializer>(
            EntityType.PLAYER,
            NBTBuilder.string("minecraft:player")
        )
    }

    @Test
    fun `reading with a non-existent id and registry with no default value should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<UnitTestSuite>()].value.read(NBTBuilder.string("oops:oops"), null)
        }
    }
}
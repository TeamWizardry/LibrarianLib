package com.teamwizardry.librarianlib.scribe.test.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.TagBuilder
import com.teamwizardry.librarianlib.scribe.nbt.RegistryEntrySerializer
import com.teamwizardry.librarianlib.scribe.test.LibLibScribeTest
import com.teamwizardry.librarianlib.testcore.junit.UnitTestSuite
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RegistryEntryTests: NbtPrismTest() {
    @Test
    fun `read+write with a mod item should be symmetrical`() {
        simple<Item, RegistryEntrySerializer<Item>>(
            LibLibScribeTest.CommonInitializer.exampleItem,
            TagBuilder.string("liblib-scribe-test:example_item")
        )
    }

    @Test
    fun `read+write with a vanilla item should be symmetrical`() {
        simple<Item, RegistryEntrySerializer<Item>>(
            Items.STONE,
            TagBuilder.string("minecraft:stone")
        )
    }

    @Test
    fun `read+write with a custom registry should be symmetrical`() {
        simple<UnitTestSuite, RegistryEntrySerializer<UnitTestSuite>>(
            LibLibScribeTest.CommonInitializer.exampleSuite,
            TagBuilder.string("liblib-scribe-test:example_suite")
        )
    }

    @Test
    fun `read+write with a vanilla block should be symmetrical`() {
        simple<Block, RegistryEntrySerializer<Block>>(
            Blocks.STONE,
            TagBuilder.string("minecraft:stone")
        )
    }

    @Test
    fun `read+write with a vanilla entity type should be symmetrical`() {
        simple<EntityType<*>, RegistryEntrySerializer<EntityType<*>>>(
            EntityType.PLAYER,
            TagBuilder.string("minecraft:player")
        )
    }

    @Test
    fun `reading with a non-existent id and registry with no default value should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<UnitTestSuite>()].value.read(TagBuilder.string("oops:oops"), null)
        }
    }
}
package com.teamwizardry.librarianlib.scribe.test

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.scribe.Scribe
import com.teamwizardry.librarianlib.scribe.nbt.RegistryEntrySerializer
import com.teamwizardry.librarianlib.scribe.test.nbt.*
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import com.teamwizardry.librarianlib.testcore.junit.UnitTestSuite
import dev.thecodewarrior.mirror.Mirror
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

internal object LibLibScribeTest {
    val logManager: ModLogManager = ModLogManager("liblib-scribe-test", "LibrarianLib Scribe Test")
    val manager: TestModContentManager = TestModContentManager("liblib-scribe-test", "Scribe", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()
        lateinit var exampleItem: Item
        lateinit var exampleSuite: UnitTestSuite

        override fun onInitialize() {
            Scribe.nbt.register(RegistryEntrySerializer(UnitTestSuite.REGISTRY, Mirror.reflect<UnitTestSuite>()))

            exampleItem = manager.create<TestItem>("example_item") {
                name = "Example Item"
                description = "(Used to test item serialization)"
            }.instance
            exampleSuite = suite("example_suite") {
                description = "(Used to test registry entry serialization)"
            }

            suite("nbt_primitives") {
                add<PrimitiveTests>()
                add<BoxedPrimitiveTests>()
                add<PrimitiveArrayTests>()
                add<EnumFactoryTests>()
            }
            suite("nbt_lists") {
                add<ArrayFactoryTests>()
                add<ListFactoryTests>()
            }
            suite("nbt_maps") {
                add<MapFactoryTests>()
            }
            suite("nbt_objects") { // todo
                add<ObjectFactoryTests>()
            }
            suite("nbt_minecraft") {
                add<RegistryEntryTests>()
                add<MinecraftSimpleTests>()
            }
            suite("nbt_librarianlib") {
                add<LibrarianLibSimpleTests>()
            }
            suite("nbt_simple") {
                add<SimpleTests>()
            }
            suite("all") {
                add<PrimitiveTests>()
                add<BoxedPrimitiveTests>()
                add<PrimitiveArrayTests>()
                add<EnumFactoryTests>()
                add<ArrayFactoryTests>()
                add<ListFactoryTests>()
                add<MapFactoryTests>()
                add<ObjectFactoryTests>()
                add<RegistryEntryTests>()
                add<MinecraftSimpleTests>()
                add<LibrarianLibSimpleTests>()
                add<SimpleTests>()
            }

            manager.registerCommon()
        }

        private fun suite(name: String, block: UnitTestSuite.() -> Unit): UnitTestSuite {
            val suite = UnitTestSuite().apply(block)
            Registry.register(UnitTestSuite.REGISTRY, manager.id(name), suite)
            return suite
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            manager.registerClient()
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}

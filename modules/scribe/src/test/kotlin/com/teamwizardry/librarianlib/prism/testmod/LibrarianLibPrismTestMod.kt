package com.teamwizardry.librarianlib.prism.testmod

import com.teamwizardry.librarianlib.prism.LibrarianLibPrismModule
import com.teamwizardry.librarianlib.prism.testmod.nbt.*
import com.teamwizardry.librarianlib.testcore.TestMod
import com.teamwizardry.librarianlib.testcore.objects.UnitTestSuite
import net.minecraftforge.fml.common.Mod

@Mod("ll-scribe-test")
object LibrarianLibPrismTestMod: TestMod(LibrarianLibPrismModule) {
    val primitiveTests: UnitTestSuite = UnitTestSuite("nbt_primitives") {
        add<PrimitiveTests>()
        add<BoxedPrimitiveTests>()
        add<PrimitiveArrayTests>()
        add<EnumFactoryTests>()
    }
    init {
        +primitiveTests
        +UnitTestSuite("nbt_lists") {
            add<ArrayFactoryTests>()
            add<ListFactoryTests>()
        }
        +UnitTestSuite("nbt_objects") { // todo
            add<ObjectFactoryTests>()
        }
        +UnitTestSuite("nbt_minecraft") {
            add<IForgeRegistryEntryTests>()
            add<INBTSerializableTests>()
            add<MinecraftSimpleTests>()
        }
        +UnitTestSuite("nbt_librarianlib") {
            add<LibrarianLibSimpleTests>()
        }
        +UnitTestSuite("nbt_simple") {
            add<SimpleTests>()
        }
        +UnitTestSuite("all") {
            add<PrimitiveTests>()
            add<BoxedPrimitiveTests>()
            add<PrimitiveArrayTests>()
            add<EnumFactoryTests>()
            add<ArrayFactoryTests>()
            add<ListFactoryTests>()
            add<ObjectFactoryTests>()
            add<IForgeRegistryEntryTests>()
            add<INBTSerializableTests>()
            add<MinecraftSimpleTests>()
            add<LibrarianLibSimpleTests>()
            add<SimpleTests>()
        }
    }
}

internal val logger = LibrarianLibPrismTestMod.makeLogger(null)

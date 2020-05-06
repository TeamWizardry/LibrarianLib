package com.teamwizardry.librarianlib.prism.testmod

import com.teamwizardry.librarianlib.prism.testmod.nbt.*
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-prism-test")
object LibrarianLibPrismTestMod: TestMod("prism", "Prism", logger) {
    init {
        +UnitTestSuite("nbt_primitives") {
            add<PrimitiveTests>()
            add<BoxedPrimitiveTests>()
            add<PrimitiveArrayTests>()
        }
        +UnitTestSuite("nbt_lists") {
            add<ArrayFactoryTests>()
            add<ListFactoryTests>()
        }
        +UnitTestSuite("nbt_objects") { // todo
            add<ObjectFactoryTests>()
        }
        +UnitTestSuite("nbt_minecraft") { // todo
            add<IForgeRegistryEntryTests>()
            add<INBTSerializableTests>()
            add<MinecraftSimpleTests>()
        }
        +UnitTestSuite("nbt_librarianlib") { // todo
            add<LibrarianLibSimpleTests>()
        }
        +UnitTestSuite("nbt_simple") { // todo
            add<SimpleTests>()
        }
    }
}

internal val logger = LogManager.getLogger("LibrarianLib: Prism Test")

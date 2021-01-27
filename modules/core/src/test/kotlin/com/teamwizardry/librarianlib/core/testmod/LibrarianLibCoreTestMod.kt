package com.teamwizardry.librarianlib.core.testmod

import com.teamwizardry.librarianlib.core.LibrarianLibCoreModule
import com.teamwizardry.librarianlib.core.testmod.tests.EasingTests
import com.teamwizardry.librarianlib.testbase.TestMod
import net.minecraftforge.fml.common.Mod

@Suppress("UNUSED_PARAMETER")
@Mod("librarianlib-test")
object LibrarianLibCoreTestMod: TestMod(LibrarianLibCoreModule) {
    init {
        +UnitTestSuite("easings") {
            add<EasingTests>()
        }
    }
}

internal val logger = LibrarianLibCoreTestMod.makeLogger(null)

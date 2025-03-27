package com.teamwizardry.librarianlib.testcore

import com.teamwizardry.librarianlib.testcore.junit.UnitTestCommand
import net.fabricmc.api.ModInitializer

internal object LLTestCoreCommon: ModInitializer {
    override fun onInitialize() {
        UnitTestCommand.register()
    }
}
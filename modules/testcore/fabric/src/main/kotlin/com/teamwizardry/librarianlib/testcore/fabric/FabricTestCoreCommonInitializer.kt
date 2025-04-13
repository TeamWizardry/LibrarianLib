package com.teamwizardry.librarianlib.testcore.fabric

import com.teamwizardry.librarianlib.testcore.TestCoreCommonInitializer
import net.fabricmc.api.ModInitializer

internal object FabricTestCoreCommonInitializer : ModInitializer {
    override fun onInitialize() {
        TestCoreCommonInitializer.onInitialize()
    }
}

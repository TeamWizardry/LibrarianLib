package com.teamwizardry.librarianlib.testcore.fabric

import com.teamwizardry.librarianlib.testcore.LibrarianLibTestCoreCommon
import net.fabricmc.api.ModInitializer

internal object TestCoreCommonInitializer : ModInitializer {
    override fun onInitialize() {
        LibrarianLibTestCoreCommon.onInitialize()
    }
}

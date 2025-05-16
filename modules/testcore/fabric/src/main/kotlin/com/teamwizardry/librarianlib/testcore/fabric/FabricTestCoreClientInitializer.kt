package com.teamwizardry.librarianlib.testcore.fabric

import com.teamwizardry.librarianlib.testcore.TestCoreClientInitializer
import net.fabricmc.api.ClientModInitializer

internal object FabricTestCoreClientInitializer : ClientModInitializer {
    override fun onInitializeClient() {
        TestCoreClientInitializer.onInitialize(true)
    }
}


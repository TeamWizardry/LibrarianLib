package com.teamwizardry.librarianlib.testcore.fabric

import com.teamwizardry.librarianlib.testcore.TestCoreServerInitializer
import net.fabricmc.api.DedicatedServerModInitializer

internal object FabricTestCoreServerInitializer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        TestCoreServerInitializer.onInitialize()
    }
}

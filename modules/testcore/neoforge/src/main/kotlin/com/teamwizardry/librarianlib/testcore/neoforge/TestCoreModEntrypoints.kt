package com.teamwizardry.librarianlib.testcore.neoforge

import com.teamwizardry.librarianlib.testcore.TestCoreClientInitializer
import com.teamwizardry.librarianlib.testcore.TestCoreCommonInitializer
import com.teamwizardry.librarianlib.testcore.TestCoreMod
import com.teamwizardry.librarianlib.testcore.TestCoreServerInitializer
import com.teamwizardry.librarianlib.testcore.platform.TestCoreClientPlatform
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(value = TestCoreMod.MODID, dist = [Dist.CLIENT])
internal class TestCoreClientMod(modBus: IEventBus, container: ModContainer) {
    init {
        TestCoreCommonInitializer.onInitialize()
        TestCoreClientInitializer.onInitialize(false)
        modBus.register(TestCoreClientPlatform.instance) // TestCoreClientPlatformImpl
    }
}

@Mod(value = TestCoreMod.MODID, dist = [Dist.DEDICATED_SERVER])
internal class TestCoreServerMod {
    init {
        TestCoreCommonInitializer.onInitialize()
        TestCoreServerInitializer.onInitialize()
    }
}

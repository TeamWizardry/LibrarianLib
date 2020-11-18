package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.capability.BaseCapability
import com.teamwizardry.librarianlib.prism.Save
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

class TestCapability: BaseCapability() {
    @Save
    var data: Int = 0

    companion object {
        @CapabilityInject(TestCapability::class)
        lateinit var capability: Capability<TestCapability>
    }
}
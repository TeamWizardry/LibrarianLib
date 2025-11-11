package com.teamwizardry.librarianlib.facade.neoforge

import com.teamwizardry.librarianlib.facade.FacadeClientInitializer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod

@Mod(value = "liblib_facade", dist = [Dist.CLIENT])
internal class LibLibFacadeMod {
    init {
        FacadeClientInitializer.initializeClient()
    }
}

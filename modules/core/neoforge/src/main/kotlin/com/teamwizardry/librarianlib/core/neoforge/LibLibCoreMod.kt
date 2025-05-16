package com.teamwizardry.librarianlib.core.neoforge

import com.teamwizardry.librarianlib.platform.ServerReloadListenerManager
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent


@Mod("liblib_core")
internal class LibLibCoreMod(modBus: IEventBus, container: ModContainer) {
    init {
        NeoForge.EVENT_BUS.addListener(::addReloadListeners)
    }

    fun addReloadListeners(event: AddReloadListenerEvent) {
        ServerReloadListenerManager.resourceReloaders.forEach(event::addListener)
    }
}

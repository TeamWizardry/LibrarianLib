package com.teamwizardry.librarianlib.facade.compat.jei

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.util.ResourceLocation

@JeiPlugin
internal class FacadeJeiPlugin: IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return loc("librarianlib:facade")
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addGuiContainerHandler(FacadeContainerScreen::class.java, JeiFacadeContainerHandler)
    }
}
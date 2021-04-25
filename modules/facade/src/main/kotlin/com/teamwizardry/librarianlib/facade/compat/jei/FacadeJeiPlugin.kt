package com.teamwizardry.librarianlib.facade.compat.jei

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IGuiHandlerRegistration
import net.minecraft.util.Identifier

@JeiPlugin
internal class FacadeJeiPlugin: IModPlugin {
    override fun getPluginUid(): Identifier {
        return loc("librarianlib:facade")
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addGuiContainerHandler(FacadeContainerScreen::class.java, JeiFacadeContainerHandler)
        registration.addGhostIngredientHandler(FacadeContainerScreen::class.java, JeiFacadeGhostIngredientHandler)
    }
}
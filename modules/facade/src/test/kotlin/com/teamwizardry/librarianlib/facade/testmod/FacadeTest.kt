package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextComponent

class FacadeTest(val name: String, val screenConstructor: (ITextComponent) -> Screen) {
    fun create(): Screen {
        return try {
            screenConstructor(StringTextComponent(name))
        } catch (e: Exception) {
            SafetyNetErrorScreen("creating test screen", e)
        }
    }
}
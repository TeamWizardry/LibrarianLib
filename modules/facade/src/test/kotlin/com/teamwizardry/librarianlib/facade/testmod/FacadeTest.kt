package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.core.util.sided.ClientMetaSupplier
import com.teamwizardry.librarianlib.core.util.sided.ClientSideFunction
import com.teamwizardry.librarianlib.facade.provided.SafetyNetErrorScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class FacadeTest(override val name: String, val screenConstructor: ClientMetaSupplier<ScreenConstructor>): FacadeTestEntry {

    override fun create(): Screen {
        return try {
            screenConstructor.getClientFunction().create(LiteralText(name))
        } catch (e: Exception) {
            SafetyNetErrorScreen("creating test screen", e)
        }
    }
}

fun interface ScreenConstructor: ClientSideFunction {
    fun create(title: Text): Screen
}
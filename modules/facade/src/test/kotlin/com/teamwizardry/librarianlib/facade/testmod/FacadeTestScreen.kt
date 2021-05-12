package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.facade.FacadeScreen
import net.minecraft.text.LiteralText

abstract class FacadeTestScreen(name: String): FacadeScreen(LiteralText(name)) {
}
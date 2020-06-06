package com.teamwizardry.librarianlib.facade.testmod

import com.teamwizardry.librarianlib.facade.FacadeScreen
import net.minecraft.util.text.StringTextComponent

abstract class FacadeTestScreen(name: String): FacadeScreen(StringTextComponent(name)) {
}
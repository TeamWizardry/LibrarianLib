package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.gui.layers.ColorLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Sprite
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestTextField : GuiBase() {
    init {
        main.size = vec(100, 100)

        val background = PastryBackground(0, 0, 100, 100)

        val fill = ColorLayer(Color.GREEN, 10, 10, 80, 10)
        val field = ComponentTextField(10, 10, 80, 10)
        field.BUS.hook<ComponentTextField.TextEditEvent> {
            try {
                it.whole.toDouble()
            } catch (ignored: Exception) {
                it.cancel()
            }
        }

        main.add(background, fill, field)
    }
}

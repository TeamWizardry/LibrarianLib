package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.layers.JeiIngredientLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class JeiIngredientLayerScreen(
    container: JeiIngredientLayerContainer,
    inventory: PlayerInventory,
    title: Text
) : FacadeContainerScreen<JeiIngredientLayerContainer>(container, inventory, title) {

    init {
        val label = PastryLabel(5, 5, "Look ma, I'm stone!")

        val box = IngredientLayer(ItemStack(Items.STONE, 1))
        box.size = label.size + vec(10, 10)
        box.add(label)

        main.size = box.size
        main.add(box)
    }

    private class IngredientLayer(val ingredient: Any) : GuiLayer(), JeiIngredientLayer {

        override fun getJeiIngredient(pos: Vec2d): Any? {
            return ingredient
        }
    }
}
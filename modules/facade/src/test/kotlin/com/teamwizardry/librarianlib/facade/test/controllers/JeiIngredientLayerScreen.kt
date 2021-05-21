package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeView
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryLabel
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

class JeiIngredientLayerScreen(
    container: JeiIngredientLayerController,
    inventory: PlayerInventory,
    title: Text
) : FacadeView<JeiIngredientLayerController>(container, inventory, title) {

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
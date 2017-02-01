package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentRect
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.client.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.client.guicontainer.builtin.BaseLayouts
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.container.GuiHandler
import com.teamwizardry.librarianlib.common.container.InventoryWrapper
import com.teamwizardry.librarianlib.common.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.common.container.builtin.SlotTypeGhost
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ContainerTest(player: EntityPlayer, tile: TEContainer) : ContainerBase(player) {

    val invPlayer = BaseWrappers.player(player)
    val invBlock = TestWrapper(tile)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).from(invPlayer.hotbar).deposit(invPlayer.head).filter {
            it.stack.item == Items.DIAMOND_HELMET
        }

        transferRule().from(invPlayer.main).deposit(invBlock.main)
        transferRule().from(invPlayer.hotbar).deposit(invBlock.small)
        transferRule().from(invBlock.main).deposit(invPlayer.main)
        transferRule().from(invBlock.small).deposit(invPlayer.hotbar)
    }

    companion object {
        val NAME = ResourceLocation("librarianlibtest:container")

        init {
            GuiHandler.registerBasicContainer(NAME, { player, pos, tile -> ContainerTest(player, tile as TEContainer) }, { player, container -> GuiContainerTest(container) })
        }
    }
}

class TestWrapper(te: TEContainer) : InventoryWrapper(te) {
    val main = slots[0..26]
    val small = slots[27..35]

    init {
        small.forEach { it.type = SlotTypeGhost(32, true)}
    }
}

class GuiContainerTest(container: ContainerTest) : GuiContainerBase(container, 197, 166) {

    val TEXTURE = Texture(ResourceLocation("librarianlibtest:textures/gui/containerTest.png"))
    val bg = TEXTURE.getSprite("bg", 197, 166)
    val slider = TEXTURE.getSprite("slider", 60, 62)

    init {
        val b = ComponentSprite(bg, 0, 0)
        mainComponents.add(b)

        val layout = BaseLayouts.player(container.invPlayer)
        b.add(layout.root)

        layout.armor.pos = vec(6, 12)
        layout.armor.isVisible = true
        layout.offhand.pos = vec(6, 84)
        layout.offhand.isVisible = true
        layout.main.pos = vec(29, 84)

        val grid = BaseLayouts.grid(container.invBlock.main, 9)
        grid.root.pos = vec(29, 12)
        b.add(grid.root)

        val s = ComponentSprite(slider, 197, 79)
        s.isVisible = false
        b.add(s)

        val miniGrid = BaseLayouts.grid(container.invBlock.small, 3)
        miniGrid.root.pos = vec(3, 5)
        s.add(miniGrid.root)

        val button = ComponentRect(178, 68, 12, 11)
        button.color.setValue(Color(0, 0, 0, 127))

        button.BUS.hook(GuiComponent.MouseClickEvent::class.java) {
            s.isVisible = !s.isVisible
        }

        b.add(button)
        // CIRCLE!!!
        /*
        grid.rows[2].pos += vec(18*4.5, 40)

        var a = 0.0
        val aFrame = (2*Math.PI)/360
        val aPer = (2*Math.PI)/9
        val radius = 30

        val row = grid.slots[2]

        grid.root.BUS.hook(GuiComponent.ComponentTickEvent::class.java) {
            a += aFrame

            row.forEachIndexed { i, slot ->
                val s = Math.sin(a + aPer*i)
                val c = Math.cos(a + aPer*i)
                slot.pos = vec(c*radius, s*radius) - vec(8, 8)
            }
        }
        */
    }
}


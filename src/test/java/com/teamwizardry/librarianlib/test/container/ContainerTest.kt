package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.client.guicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.client.guicontainer.builtin.BaseLayouts
import com.teamwizardry.librarianlib.client.sprite.Texture
import com.teamwizardry.librarianlib.common.container.ContainerBase
import com.teamwizardry.librarianlib.common.container.GuiHandler
import com.teamwizardry.librarianlib.common.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.util.plus
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by TheCodeWarrior
 */
class ContainerTest(player: EntityPlayer, tile: TEContainer) : ContainerBase(player) {

    val invPlayer = BaseWrappers.player(player.inventory)
    val invBlock = BaseWrappers.inventory(tile)

    override fun addTo(impl: ContainerImpl) {
        impl.addSlots(invPlayer)
        impl.addSlots(invBlock)
    }


    companion object {
        val NAME = ResourceLocation("librarianlibtest:container")
        init {
            GuiHandler.registerBasicContainer(NAME, { player, pos, tile -> ContainerTest(player, tile as TEContainer)}, { player, container -> GuiContainerTest(container) })
        }
    }
}


class GuiContainerTest(container: ContainerTest) : GuiContainerBase(container, 197, 166) {

    val TEXTURE = Texture(ResourceLocation("librarianlibtest:textures/gui/containerTest.png"))
    val bg = TEXTURE.getSprite("bg", 197, 166)

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

        val grid = BaseLayouts.grid(container.invBlock, 9)
        grid.root.pos = vec(29, 12)
        b.add(grid.root)


        var bool = false
        b.BUS.hook(GuiComponent.MouseClickEvent::class.java) {
            if(bool) {
                b.pos += vec(10, 10)
            } else {
                b.pos -= vec(10, 10)
            }
            bool = !bool
        }
    }
}


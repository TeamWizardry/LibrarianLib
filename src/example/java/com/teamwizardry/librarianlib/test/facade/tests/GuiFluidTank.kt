package com.teamwizardry.librarianlib.test.facade.tests

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.layers.minecraft.FluidGaugeLayer
import com.teamwizardry.librarianlib.features.facadecontainer.ComponentSlot
import com.teamwizardry.librarianlib.features.facadecontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.facadecontainer.builtin.BaseLayouts
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.sprite.Texture
import com.teamwizardry.librarianlib.test.container.FluidTankContainer
import com.teamwizardry.librarianlib.test.container.TEFluidTank
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * The gui for our fluid tank.
 * Nothing fancy here ^-^
 */
open class GuiFluidTank(inventorySlotsIn: FluidTankContainer) : GuiContainerBase(inventorySlotsIn) {

    init {
        val te = inventorySlotsIn.invBlock.inventory as TEFluidTank
        val bg = ComponentSprite(BG, 0, 0)
        main.size = vec(176, 166)
        main.add(bg)

        val inventory = BaseLayouts.player(inventorySlotsIn.invPlayer)
        bg.add(inventory.root)
        inventory.main.pos = vec(8, 84)

        bg.add(ComponentSprite(SLOT, 50, 33))
        bg.add(ComponentSprite(SLOT, 100, 33))

        val input = ComponentSlot(inventorySlotsIn.invBlock.input, 51, 34)
        bg.add(input)

        val output = ComponentSlot(inventorySlotsIn.invBlock.output, 101, 34)
        bg.add(output)

        val state = te.world.getBlockState(te.pos)
        val title = ComponentText(88, 6, horizontal = ComponentText.TextAlignH.CENTER)
        title.text = I18n.format(ItemStack(state.block, 1, state.block.damageDropped(state)).displayName)
        bg.add(title)

        val fluidBg = SpriteLayer(FLUID_BG, 15, 15)
        fluidBg.width = 16.0
        val fluidBar = FluidGaugeLayer(15, 15)
        val tankProps = te.fluidHandler.tankPropertiesImpl[0]
        fluidBar.fluid_im {
            tankProps.contents?.fluid
        }
        fluidBar.fillFraction_im {
            (tankProps.contents?.amount?.toDouble() ?: 0.0) / Math.max(1.0, tankProps.capacity.toDouble())
        }
        fluidBar.direction = Cardinal2d.UP
        fluidBar.tooltip_im {
            val f = te.fluidHandler.fluid
            listOf(if (f != null) I18n.format("llt:gui.fluid", f.localizedName, f.amount) else I18n.format("llt:gui.fluidEmpty"))
        }
        bg.add(fluidBg, fluidBar)
    }

    companion object {
        private val TEX = Texture(ResourceLocation("librarianlibtest", "textures/guis/gui_powered_machine.png"), 256, 256)
        private val BG = TEX.getSprite("bg")

        private val FLUID_BG = TEX.getSprite("power_bg")

        private val SLOT = TEX.getSprite("slot")
    }
}

package com.teamwizardry.librarianlib.test.facade.tests

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.facade.components.ComponentProgressBar
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentSpriteProgressBar
import com.teamwizardry.librarianlib.features.facade.components.ComponentText
import com.teamwizardry.librarianlib.features.neoguicontainer.ComponentSlot
import com.teamwizardry.librarianlib.features.neoguicontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.neoguicontainer.builtin.BaseLayouts
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Texture
import com.teamwizardry.librarianlib.test.container.PoweredMachineContainer
import com.teamwizardry.librarianlib.test.container.TEPoweredMachine
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * The gui for our powered machine.
 * Nothing fancy here ^-^
 */
open class GuiPoweredMachine(inventorySlotsIn: PoweredMachineContainer) : GuiContainerBase(inventorySlotsIn, 176, 166) {

    init {
        val te = inventorySlotsIn.invBlock.inventory as TEPoweredMachine
        val bg = ComponentSprite(BG, 0, 0)
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
        title.text = (I18n.format(ItemStack(state.block, 1, state.block.damageDropped(state)).displayName))
        bg.add(title)

        val progressBar = ComponentProgressBar(PROGRESS_FG, PROGRESS_BG, 77, 37)
        progressBar.progress_im { te.currentOperation?.progress ?: 0.0 }
        progressBar.tooltip_im { listOf(I18n.format("llt:gui.progress", ((te.currentOperation?.progress ?: 0.0) * 100).toInt())) }
        bg.add(progressBar)

        val powerBar = ComponentProgressBar(POWER_FG, POWER_BG, 15, 15)
        powerBar.direction = ComponentSpriteProgressBar.ProgressDirection.Y_NEG
        powerBar.progress_im { te.energyHandler.energyStored.toDouble() / te.energyHandler.maxEnergyStored }
        powerBar.tooltip_im { listOf(I18n.format("llt:gui.energy", te.energyHandler.energyStored, te.energyHandler.maxEnergyStored)) }
        bg.add(powerBar)
    }

    companion object {
        private val TEX = Texture(ResourceLocation("librarianlibtest", "textures/guis/gui_powered_machine.png"), 256, 256)
        private val BG = TEX.getSprite("bg")

        private val PROGRESS_BG = TEX.getSprite("progression_bg")
        private val PROGRESS_FG = TEX.getSprite("progression_fg")

        private val POWER_BG = TEX.getSprite("power_bg")
        private val POWER_FG = TEX.getSprite("power_fg")

        private val SLOT = TEX.getSprite("slot")
    }
}

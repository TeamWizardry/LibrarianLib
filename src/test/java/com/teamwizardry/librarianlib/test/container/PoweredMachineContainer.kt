package com.teamwizardry.librarianlib.test.container

/*
 * Created by Bluexin.
 * Made for LibrarianLib, under GNU LGPL v3.0
 * (a copy of which can be found at the repo root)
 */

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.test.gui.tests.GuiPoweredMachine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 * Container for the gui of our [TEPoweredMachine].
 */
class PoweredMachineContainer(player: EntityPlayer, te: TEPoweredMachine) : ContainerBase(player) {

    val invPlayer = BaseWrappers.player(player)
    val invBlock = PoweredMachineWrapper(te)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).from(invPlayer.hotbar).deposit(invBlock.input)
        transferRule().from(invBlock.input).from(invBlock.output).deposit(invPlayer.main).deposit(invPlayer.hotbar)
    }

    companion object {
        val NAME = ResourceLocation("librarianlibtest", "poweredmachinecontainer")

        init {
            GuiHandler.registerBasicContainer(NAME, { player, _, tile -> PoweredMachineContainer(player, tile as TEPoweredMachine) }, { _, container -> GuiPoweredMachine(container) })
        }
    }
}

class PoweredMachineWrapper(inventory: TEPoweredMachine) : InventoryWrapper(inventory) {
    val input = slots[TEPoweredMachine.SLOT_IN]
    val output = slots[TEPoweredMachine.SLOT_OUT]
}

package com.teamwizardry.librarianlib.core.client.commands

import com.teamwizardry.librarianlib.features.base.SimpleCommand
import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.hud.GuiHud

object GuiCommands {
    val root = SimpleCommandTree("gui", "librarianlib.command.liblib.gui.usage")

    init {
        root.addSubcommand(
            SimpleCommand("reloadhud", "librarianlib.command.liblib.gui.reloadhud.usage") { _, _, _ ->
                GuiHud.reload()
            }
        )

        root.addSubcommand(GuiOptionCommand)
        GuiOptionCommand.options.add(BooleanGuiOption("showDebugTilt", GuiLayer.Companion::showDebugTilt))
        GuiOptionCommand.options.add(BooleanGuiOption("showDebugBoundingBox", GuiLayer.Companion::showDebugBoundingBox))
    }
}



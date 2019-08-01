package com.teamwizardry.librarianlib.core.client.commands

import com.teamwizardry.librarianlib.features.base.SimpleCommand
import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.hud.GuiHud
import com.teamwizardry.librarianlib.features.sprite.Texture

object GuiCommands {
    val root = SimpleCommandTree("gui", "librarianlib.command.liblib.gui.usage")

    init {
        root.addSubcommand(
            SimpleCommand("reloadhud", "librarianlib.command.liblib.gui.reloadhud.usage") { _, _, _ ->
                GuiHud.reload()
            }
        )
        root.addSubcommand(
            SimpleCommand("reloadTextures", "librarianlib.command.liblib.gui.reloadTextures.usage") { _, _, _ ->
                Texture.reloadTextures()
            }
        )

        val options = OptionCommand("options")
        root.addSubcommand(options)
        options.add(BooleanGuiOption("showDebugTilt", GuiLayer.Companion::showDebugTilt))
        options.add(BooleanGuiOption("showDebugBoundingBox", GuiLayer.Companion::showDebugBoundingBox))
        options.add(BooleanGuiOption("showLayoutOverlay", GuiLayer.Companion::showLayoutOverlay))
    }
}



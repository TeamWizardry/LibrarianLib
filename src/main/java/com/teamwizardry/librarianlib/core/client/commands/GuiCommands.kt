package com.teamwizardry.librarianlib.core.client.commands

import com.teamwizardry.librarianlib.features.base.SimpleCommand
import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.hud.GuiHud
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

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



package com.teamwizardry.librarianlib.core.client.commands

import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import net.minecraft.command.ICommandSender
import net.minecraftforge.server.command.CommandTreeBase

internal object ClientCommands {
    val root = SimpleCommandTree("liblib", "librarianlib.command.liblib.usage")

    init {
        root.addSubcommand(GuiCommands.root)
        root.addSubcommand(ShaderCommands.root)
    }
}
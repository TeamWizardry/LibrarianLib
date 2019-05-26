package com.teamwizardry.librarianlib.core.common.commands

import com.teamwizardry.librarianlib.features.base.PermissionLevel
import com.teamwizardry.librarianlib.features.base.SimpleCommandTree
import net.minecraft.command.ICommandSender
import net.minecraftforge.server.command.CommandTreeBase

internal object ServerCommands {
    val root = SimpleCommandTree("liblibadmin", "librarianlib.command.liblibadmin.usage", PermissionLevel.MULTIPLAYER_ADMIN)

    init {

    }
}

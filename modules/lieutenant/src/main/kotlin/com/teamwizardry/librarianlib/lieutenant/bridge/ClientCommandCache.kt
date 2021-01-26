package com.teamwizardry.librarianlib.lieutenant.bridge

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.teamwizardry.librarianlib.lieutenant.ClientCommandSource
import com.teamwizardry.librarianlib.lieutenant.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import java.util.*

/**
 * Used to check if a command is a client command and for executing commands on the client. Client commands are still
 * registered with the main command
 */
public object ClientCommandCache {
    private val dispatcher: CommandDispatcher<ClientCommandSource> = CommandDispatcher()

    public fun build() {
        MinecraftForge.EVENT_BUS.post(RegisterClientCommandsEvent(dispatcher))
    }

    @Throws(CommandSyntaxException::class)
    public fun execute(input: String, source: ClientCommandSource): Int {
        return dispatcher.execute(input, source)
    }

    public fun hasCommand(name: String): Boolean {
        return dispatcher.findNode(Collections.singleton(name)) != null
    }
}

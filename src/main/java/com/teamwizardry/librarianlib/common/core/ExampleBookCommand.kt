package com.teamwizardry.librarianlib.common.core

import com.teamwizardry.librarianlib.LibrarianLib
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer

/**
 * Created by TheCodeWarrior on 7/31/16.
 */
class ExampleBookCommand : CommandBase() {

    override fun getName(): String {
        return "liblibBook"
    }

    override fun getUsage(p_71518_1_: ICommandSender): String {
        return "/liblibBook"
    }

    // Older versions (<1.9) had instead of execute method processCommand method (without MinecraftServer parameter)
    @Throws(WrongUsageException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        server.addScheduledTask { LibrarianLib.PROXY.bookInstance?.display() }

    }

    @Throws(WrongUsageException::class)
    fun wrongUsage(sender: ICommandSender) {
        throw WrongUsageException(getUsage(sender))
    }
}

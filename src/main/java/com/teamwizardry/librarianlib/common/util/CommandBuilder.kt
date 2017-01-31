package com.teamwizardry.librarianlib.common.util

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

/**
 * Created by Elad on 1/31/2017.
 */
class CommandBuilder(val commandName: String) : CommandBase() {
    override fun getName(): String {
        return commandName
    }

    override fun execute(p0: MinecraftServer, p1: ICommandSender, p2: Array<out String>) {
        executer(p0, p1, p2)
    }

    override fun getUsage(p0: ICommandSender?): String {
        return usage
    }

    override fun checkPermission(a: MinecraftServer?, b: ICommandSender?): Boolean {
        return checkPermission(a, b)
    }

    var usage = ""
    @JvmName("setCommandUsage")
    fun setUsage(usage: String): CommandBuilder {
        this.usage = usage
        return this
    }

    var executer: (MinecraftServer, ICommandSender, Array<out String>) -> Unit = { a, b, c -> }
    @JvmName("setExecutingLambda")
    fun setExecuter(executer: (MinecraftServer, ICommandSender, Array<out String>) -> Unit): CommandBuilder {
        this.executer = executer
        return this
    }

    var checkPermission: (MinecraftServer, ICommandSender) -> Boolean = { a, b -> false }
    fun setPermissionChecker(checkPermission: (MinecraftServer, ICommandSender) -> Boolean): CommandBuilder {
        this.checkPermission = checkPermission
        return this
    }

    fun register() {
        if (isIsTooLate) throw IllegalStateException("Command ${javaClass.name} registered too late!")
        commands.add(this)
    }

    companion object {
        val commands = mutableListOf<CommandBuilder>()
        internal var isIsTooLate = false
        //Is it too late now to say sorry?
        //'Cause I'm, missing more than just your body.
    }
}
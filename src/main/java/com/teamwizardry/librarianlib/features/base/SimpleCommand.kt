package com.teamwizardry.librarianlib.features.base

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer

class SimpleCommand(
    private val name: String, private val usage: String, private val permissionLevel: PermissionLevel,
    private val executor: SimpleCommand.(server: MinecraftServer, sender: ICommandSender, args: Array<String>) -> Unit
): CommandBase() {
    constructor(name: String, usage: String,
        executor: SimpleCommand.(server: MinecraftServer, sender: ICommandSender, args: Array<String>) -> Unit
    ): this(name, usage, PermissionLevel.NONE, executor)

    private var server: MinecraftServer? = null
    private var sender: ICommandSender? = null
    private var args: Array<String>? = null

    override fun getName(): String = this.name
    override fun getUsage(sender: ICommandSender): String = this.usage

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        this.server = server
        this.sender = sender
        this.args = args

        this.executor(server, sender, args)

        this.server = null
        this.sender = null
        this.args = null
    }

    override fun getRequiredPermissionLevel(): Int {
        return permissionLevel.ordinal
    }

    /**
     * Throws a usage error
     */
    fun throwUsage(): Nothing {
        throw WrongUsageException(this.sender?.let { this.getUsage(it) } ?: this.usage)
    }

    /**
     * Throws if the argument count is not equal to [count]
     */
    fun assertCount(count: Int) {
        if(args?.size != count) throwUsage()
    }

    /**
     * Throws if the argument count is less than [min] or greater than [max]
     */
    fun assertCount(min: Int, max: Int) {
        if(args?.size?.let { it in min..max } != true) throwUsage()
    }
}

enum class PermissionLevel {
    NONE,
    BYPASS_SPAWN,
    SINGLEPLAYER_CHEATS,
    MULTIPLAYER_ADMIN,
    SERVER_MANAGER;

    companion object {
        @JvmStatic
        fun get(level: Int): PermissionLevel? {
            return PermissionLevel.values().getOrNull(level)
        }
    }
}
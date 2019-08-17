package com.teamwizardry.librarianlib.features.base

import net.minecraft.command.ICommandSender
import net.minecraftforge.server.command.CommandTreeBase

class SimpleCommandTree(
    private val name: String, private val usage: String, private val permissionLevel: PermissionLevel
): CommandTreeBase() {
    constructor(name: String, usage: String): this(name, usage, PermissionLevel.NONE)

    override fun getName(): String = this.name
    override fun getUsage(sender: ICommandSender): String = this.usage

    override fun getRequiredPermissionLevel(): Int {
        return permissionLevel.ordinal
    }
}
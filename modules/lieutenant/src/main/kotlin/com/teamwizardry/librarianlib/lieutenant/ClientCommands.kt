package com.teamwizardry.librarianlib.lieutenant

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder

/**
 * Provides replacements for the [net.minecraft.command.Commands] argument methods.
 */
public object ClientCommands {
    @JvmStatic
    public fun literal(name: String): LiteralArgumentBuilder<ClientCommandSource> {
        return LiteralArgumentBuilder.literal(name)
    }

    @JvmStatic
    public fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<ClientCommandSource, T> {
        return RequiredArgumentBuilder.argument(name, type)
    }
}
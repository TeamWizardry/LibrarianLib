package com.teamwizardry.librarianlib.lieutenant

import com.mojang.brigadier.CommandDispatcher
import net.minecraftforge.eventbus.api.Event

/**
 * Add client-only commands to the given dispatcher. This will be called multiple times, and it *must* produce the same
 * commands every time.
 */
public class RegisterClientCommandsEvent(public val dispatcher: CommandDispatcher<ClientCommandSource>): Event()

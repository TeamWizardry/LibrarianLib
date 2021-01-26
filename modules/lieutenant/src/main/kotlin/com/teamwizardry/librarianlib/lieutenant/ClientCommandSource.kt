package com.teamwizardry.librarianlib.lieutenant

import net.minecraft.command.ISuggestionProvider
import net.minecraft.util.text.ITextComponent

/**
 * Contains methods for sending messages to the player.
 * Alternative to `CommandSource`.
 */
public interface ClientCommandSource : ISuggestionProvider {
    /**
     * Sends a message to the player (mirrors [net.minecraft.command.CommandSource.logFeedback]).
     * Equivalent of calling `sendFeedback(message, false)`.
     *
     * @param message the message
     */
    public fun logFeedback(message: ITextComponent)

    /**
     * Sends a message to the player (mirrors [net.minecraft.command.CommandSource.sendFeedback]).
     *
     * @param message the message
     * @param actionBar true, if the message is displayed on the action bar.
     * @since 1.0.0
     */
    public fun sendFeedback(message: ITextComponent, actionBar: Boolean)

    /**
     * Sends an error message to the player (mirrors [net.minecraft.command.CommandSource.sendErrorMessage]).
     *
     * @param text the message
     */
    public fun sendErrorMessage(text: ITextComponent)
}
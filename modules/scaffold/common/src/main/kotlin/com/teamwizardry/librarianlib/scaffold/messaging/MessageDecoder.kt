package com.teamwizardry.librarianlib.facade.container.messaging

/**
 * An object that can decode and execute message packets
 */
public class MessageDecoder(
    /**
     * The instance to scan for and run messages on
     */
    private val instance: Any,
    /**
     * The window ID, which is used to verify messages so they aren't executed on the incorrect screen
     */
    private val windowId: Int
) {
    private val targetType = instance.javaClass
    private val messages = MessageScanner.getMessages(targetType).associateBy { it.name }

    /**
     * Encode a message into a packet. Used for transmitting messages from the GUI to the server container as well as
     * between the client and server containers.
     */
    public fun execute(packet: MessagePacket) {
        if(packet.windowId != windowId)
            return // message was meant for a different screen, just ignore it
        val message = messages[packet.name] ?: throw IllegalArgumentException("${targetType.simpleName} has no message named '${packet.name}'")
        if(!message.side.isValid(packet.side)) {
            throw IllegalStateException("Illegal side ${packet.side} for message ${packet.name} (side = ${packet.side})")
        }
        message.method.call<Any?>(instance, *message.readArguments(packet.payload))
    }
}

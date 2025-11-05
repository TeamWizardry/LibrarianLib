package com.teamwizardry.librarianlib.facade.container.messaging

/**
 * An object that can encode and send message packets
 */
public class MessageEncoder(
    private val targetType: Class<*>,
    /**
     * The window ID, which is used to verify messages so they aren't executed on the incorrect screen
     */
    private val windowId: Int
) {
    private val messages = MessageScanner.getMessages(targetType).associateBy { it.name }

    /**
     * Encode a message into a packet. Used for transmitting messages from the GUI to the server container as well as
     * between the client and server containers.
     */
    public fun encode(name: String, arguments: Array<out Any?>): MessagePacket {
        val message = messages[name] ?: throw IllegalArgumentException("${targetType.simpleName} has no message named '$name'")
        return MessagePacket(windowId, name, message.writeArguments(arguments))
    }

    /**
     * Directly invoke a message on an instance. Used to send messages from the GUI to the client-side container.
     */
    public fun invoke(instance: Any, name: String, arguments: Array<out Any?>) {
        val message = messages[name] ?: throw IllegalArgumentException("${targetType.simpleName} has no message named '$name'")
        message.method.call<Any?>(instance, *arguments)
    }
}


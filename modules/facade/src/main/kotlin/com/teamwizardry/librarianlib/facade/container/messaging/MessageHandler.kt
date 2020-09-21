package com.teamwizardry.librarianlib.facade.container.messaging

/**
 * An interface for containers that can receive Facade messages. The actual message processing should be delegated to
 * an instance of [MessageDecoder].
 */
public interface MessageHandler {
    public fun receiveMessage(packet: MessagePacket)
}
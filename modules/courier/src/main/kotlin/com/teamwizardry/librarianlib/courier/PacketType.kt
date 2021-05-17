package com.teamwizardry.librarianlib.courier

import net.minecraft.util.Identifier

public abstract class PacketType<T : Any>(
    /**
     * The packet channel
     */
    public val channel: Identifier,
    /**
     * The packet class
     */
    public val type: Class<T>,
) {
    /**
     * Encode the [packet] into the given buffer
     */
    public abstract fun encode(packet: T, buffer: CourierBuffer)

    /**
     * Decode a new packet from the given buffer
     */
    public abstract fun decode(buffer: CourierBuffer): T
}

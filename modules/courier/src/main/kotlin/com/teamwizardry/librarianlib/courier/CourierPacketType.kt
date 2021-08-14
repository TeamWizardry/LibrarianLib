package com.teamwizardry.librarianlib.courier

import com.teamwizardry.librarianlib.scribe.Scribe
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.annotation.RefractClass
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * A class to encode/decode courier packets
 */
public class CourierPacketType<T : CourierPacket>(
    identifier: Identifier,
    type: Class<T>,
) : PacketType<T>(identifier, type) {
    init {
        if (!type.isAnnotationPresent(RefractClass::class.java))
            throw IllegalArgumentException("Courier packets must be annotated with @RefractClass")
    }

    private val serializer = Scribe.nbt[Mirror.reflect(type)].value

    override fun encode(packet: T, buffer: CourierBuffer) {
        val tag = serializer.write(packet) as NbtCompound
        buffer.writeNbt(tag)
        packet.writeBytes(buffer)
    }

    override fun decode(buffer: CourierBuffer): T {
        val tag = buffer.readNbt() ?: throw IllegalStateException("Packet didn't start with a compound tag")

        @Suppress("UNCHECKED_CAST")
        val packet = serializer.read(tag, null) as T
        packet.readBytes(buffer)
        return packet
    }
}
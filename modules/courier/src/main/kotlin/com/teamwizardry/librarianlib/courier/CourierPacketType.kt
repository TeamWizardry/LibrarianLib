package com.teamwizardry.librarianlib.courier

import com.teamwizardry.librarianlib.prism.Prisms
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.annotation.RefractClass
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkEvent
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.function.BiConsumer
import java.util.function.Supplier

/**
 * A packet type that uses Prism serialization
 */
internal class CourierPacketType<T: Any>(type: Class<T>, val handler: BiConsumer<T, NetworkEvent.Context>): PacketType<T>(type) {
    init {
        if(!type.isAnnotationPresent(RefractClass::class.java))
            throw IllegalArgumentException("Courier packets must be annotated with @RefractClass")
    }
    private val serializer by Prisms.nbt[Mirror.reflect(type)]

    override fun encode(packet: T, buffer: CourierBuffer) {
        val tag = serializer.write(packet) as CompoundNBT
        buffer.writeCompoundTag(tag)
        (packet as? CourierPacket)?.writeBytes(buffer)
    }

    override fun decode(buffer: CourierBuffer): T {
        val tag = buffer.readCompoundTag() ?: throw IllegalStateException("Packet didn't start with a compound tag")
        val packet = serializer.read(tag, null)
        (packet as? CourierPacket)?.readBytes(buffer)
        @Suppress("UNCHECKED_CAST")
        return packet as T
    }

    override fun handle(packet: T, context: Supplier<NetworkEvent.Context>) {
        val realContext = context.get() // because for some ungodly reason they use a supplier... which only ever returns a fixed value
        handler.accept(packet, realContext)
        (packet as? CourierPacket)?.handle(realContext)
    }
}
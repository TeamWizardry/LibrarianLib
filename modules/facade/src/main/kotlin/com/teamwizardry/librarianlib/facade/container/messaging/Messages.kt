package com.teamwizardry.librarianlib.facade.container.messaging

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.inconceivable
import com.teamwizardry.librarianlib.courier.CourierBuffer
import com.teamwizardry.librarianlib.courier.PacketType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import java.util.function.Supplier

@Target(AnnotationTarget.FUNCTION)
public annotation class Message(val name: String = "", val side: MessageSide = MessageSide.BOTH) {
}

public enum class MessageSide {
    CLIENT, SERVER, BOTH;

    public fun isValid(side: MessageSide): Boolean {
        return this == BOTH || this == side
    }
}

public data class MessagePacket(val windowId: Int, val name: String, val payload: NbtCompound) {
    var side: MessageSide = MessageSide.BOTH
}

internal object MessagePacketType :
    PacketType<MessagePacket>(Identifier("liblib-facade:container_message"), MessagePacket::class.java) {

    override fun encode(packet: MessagePacket, buffer: CourierBuffer) {
        buffer.writeVarInt(packet.windowId)
        buffer.writeString(packet.name)
        buffer.writeNbt(packet.payload)
    }

    override fun decode(buffer: CourierBuffer): MessagePacket {
        val windowId = buffer.readVarInt()
        val name = buffer.readString()
        val payload = buffer.readNbt()!!
        return MessagePacket(windowId, name, payload)
    }
}

package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import com.teamwizardry.librarianlib.courier.PacketType
import com.teamwizardry.librarianlib.prism.Prisms
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.prism.PrismException
import dev.thecodewarrior.prism.utils.annotation
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.lang.IllegalStateException
import java.util.function.Supplier

public class ContainerMessager(private val receiver: Any, private val windowId: Int, private val isClient: Boolean) {
    public fun send(name: String, vararg parameters: Any) {

    }

    private object MessageScanner {

    }

    private class MessageScan(val method: MethodMirror) {
        val name = method.annotation<Message>()?.name ?: method.name
        val parameterSerializers = method.parameters.map { parameter ->
            try {
                Prisms.nbt[parameter.type].value
            } catch(e: PrismException) {
                throw IllegalStateException("Error getting serializer for parameter ${parameter.index} of message '$name'")
            }
        }
    }
}

@Target(AnnotationTarget.FUNCTION)
public annotation class Message(val name: String = "")

private object MessagePacketType: PacketType<MessagePacket>(MessagePacket::class.java) {
    override fun encode(packet: MessagePacket, buffer: PacketBuffer) {
        buffer.writeVarInt(packet.windowId)
        buffer.writeString(packet.name)
        buffer.writeCompoundTag(packet.payload)
    }

    override fun decode(buffer: PacketBuffer): MessagePacket {
        val windowId = buffer.readVarInt()
        val name = buffer.readString()
        val payload = buffer.readCompoundTag()!!
        return MessagePacket(windowId, name, payload)
    }

    override fun handle(packet: MessagePacket, context: Supplier<NetworkEvent.Context>) {
        val container = if(context.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
            clientOnly {
                Client.minecraft.player?.openContainer as? FacadeContainer
            }
        } else {
            context.get().sender?.openContainer as? FacadeContainer
        }
        container
    }
}

public data class MessagePacket(val windowId: Int, val name: String, val payload: CompoundNBT) {

}
package com.teamwizardry.librarianlib.facade.container.messaging

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import com.teamwizardry.librarianlib.courier.PacketType
import com.teamwizardry.librarianlib.facade.container.FacadeContainer
import com.teamwizardry.librarianlib.prism.Prisms
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.MethodMirror
import dev.thecodewarrior.prism.PrismException
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.network.PacketBuffer
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.function.Supplier

@Target(AnnotationTarget.FUNCTION)
public annotation class Message(val name: String = "")

public data class MessagePacket(val windowId: Int, val name: String, val payload: CompoundNBT)

internal object MessagePacketType: PacketType<MessagePacket>(MessagePacket::class.java) {
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
        val player: PlayerEntity? = if(context.get().direction == NetworkDirection.PLAY_TO_CLIENT) {
            clientOnly { Client.minecraft.player }
        } else {
            context.get().sender
        }
        context.get().enqueueWork {
            (player?.openContainer as? MessageHandler)?.receiveMessage(packet)
        }
    }
}

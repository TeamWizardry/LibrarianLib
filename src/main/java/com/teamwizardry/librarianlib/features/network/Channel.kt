package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.kotlin.DefaultedMutableMap
import com.teamwizardry.librarianlib.features.kotlin.withRealDefault
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper

typealias PerIdentifier = PacketAbstractUpdate
typealias PerEntity = MutableMap<Any, PerIdentifier>
typealias PerPlayer = DefaultedMutableMap<Class<*>, PerEntity>

const val SERVER_TO_CLIENT_MAX: Int = 2097050
const val CLIENT_TO_SERVER_MAX: Int = 32767

class Channel(private val network: SimpleNetworkWrapper) {
    private val updates = // updates[player][packetType][identifier] = packet
            mutableMapOf<EntityPlayerMP, PerPlayer>().withRealDefault {
                mutableMapOf<Class<*>, PerEntity>().withRealDefault {
                    mutableMapOf<Any, PerIdentifier>()
                }
            }
    private val clientToServerUpdates =
            mutableMapOf<Class<*>, PerEntity>().withRealDefault {
                mutableMapOf<Any, PerIdentifier>()
            }


    fun send(target: PacketTarget, vararg packets: PacketBase) {
        if (target == TargetServer) {
            packets.forEach {
                network.sendToServer(it)
            }
        } else {
            target.players.forEach { player ->
                if (player is EntityPlayerMP)
                    packets.forEach {
                        network.sendTo(it, player)
                    }
            }
        }
    }

    fun <T : PacketBase> bundle(target: PacketTarget, vararg packets: T) {
        if (target == TargetServer) {
            val list = PacketBundle.compactPackets(packets.toList(), CLIENT_TO_SERVER_MAX)
            list.forEach {
                network.sendToServer(it)
            }
        } else {
            target.players.forEach { player ->
                val list = PacketBundle.compactPackets(packets.toList(), SERVER_TO_CLIENT_MAX)
                if (player is EntityPlayerMP)
                    list.forEach {
                        network.sendTo(it, player)
                    }
            }
        }
    }

    fun update(target: PacketTarget, packet: PacketAbstractUpdate) {
        val ident = packet.identifier ?: throw IllegalArgumentException("Packet has no identifier!")
        target.players.forEach { player ->
            if (player is EntityPlayerMP)
                updates[player][packet.javaClass][ident] = packet
        }
    }

    fun onTickEnd() {
        updates.forEach { player, types ->
            types.forEach { _, packets ->
                val list = PacketBundle.compactPackets(packets.values, SERVER_TO_CLIENT_MAX)
                list.forEach {
                    network.sendTo(it, player)
                }
                packets.clear()
            }
        }
        clientToServerUpdates.forEach { _, packets ->
            val list = PacketBundle.compactPackets(packets.values, SERVER_TO_CLIENT_MAX)
            list.forEach {
                network.sendToServer(it)
            }
            packets.clear()
        }
    }

}

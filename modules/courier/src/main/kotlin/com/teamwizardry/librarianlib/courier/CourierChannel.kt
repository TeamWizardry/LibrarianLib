package com.teamwizardry.librarianlib.courier

import net.minecraft.util.Identifier
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.PacketDistributor
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Predicate

public class CourierChannel(private val name: Identifier, private var version: String) {
    private var clientAcceptedVersions: Predicate<String> = Predicate { it == version }
    private var serverAcceptedVersions: Predicate<String> = Predicate { it == version }

    private val baseChannel = NetworkRegistry.newSimpleChannel(
        name, ::version, ::clientAcceptedVersions, ::serverAcceptedVersions
    )
    private val packets = mutableListOf<PacketType<*>>()

    private fun clientAcceptedVersions(other: String): Boolean {
        return this.clientAcceptedVersions.test(other)
    }

    private fun serverAcceptedVersions(other: String): Boolean {
        return this.serverAcceptedVersions.test(other)
    }

    public fun configureVersion(
        version: String,
        clientAcceptedVersions: Predicate<String>,
        serverAcceptedVersions: Predicate<String>
    ) {
        this.version = version
        this.clientAcceptedVersions = clientAcceptedVersions
        this.serverAcceptedVersions = serverAcceptedVersions
    }

    public fun sendToServer(packet: Any) {
        baseChannel.sendToServer(packet)
    }

    public fun send(distributor: PacketDistributor.PacketTarget, packet: Any) {
        baseChannel.send(distributor, packet)
    }

    public fun reply(message: Any, context: NetworkEvent.Context) {
        baseChannel.reply(message, context)
    }

    public fun <T> register(packetType: PacketType<T>) {
        packetType.index = packets.size
        packets.add(packetType)
        @Suppress("INACCESSIBLE_TYPE")

        baseChannel.registerMessage(
            packetType.index,
            packetType.type,
            { packet, buffer -> packetType.encode(packet, CourierBuffer(buffer)) },
            { buffer -> packetType.decode(CourierBuffer(buffer)) }
        ) { packet, context ->
            // because for some ungodly reason you have to do this yourself, otherwise you get spammed with
            // "Unknown custom packet identifier" log messages
            context.get().packetHandled = true
            packetType.handle(packet, context)
        }
    }

    public fun <T> register(packetType: PacketType<T>, direction: NetworkDirection?) {
        packetType.index = packets.size
        packets.add(packetType)
        @Suppress("INACCESSIBLE_TYPE")
        baseChannel.registerMessage(
            packetType.index,
            packetType.type,
            { packet, buffer -> packetType.encode(packet, CourierBuffer(buffer)) },
            { buffer -> packetType.decode(CourierBuffer(buffer)) },
            { packet, context ->
                // because for some ungodly reason you have to do this yourself, otherwise you get spammed with
                // "Unknown custom packet identifier" log messages
                context.get().packetHandled = true
                packetType.handle(packet, context)
            },
            Optional.ofNullable(direction)
        )
    }

    public fun <T: Any> registerCourierPacket(
        type: Class<T>,
        direction: NetworkDirection?,
        handler: BiConsumer<T, NetworkEvent.Context>
    ) {
        register(CourierPacketType(type, handler), direction)
    }

    public fun <T: Any> registerCourierPacket(type: Class<T>, handler: BiConsumer<T, NetworkEvent.Context>) {
        register(CourierPacketType(type, handler))
    }

    public fun <T: Any> registerCourierPacket(type: Class<T>, direction: NetworkDirection?) {
        register(CourierPacketType(type) { _, _ -> }, direction)
    }

    public fun <T: Any> registerCourierPacket(type: Class<T>) {
        register(CourierPacketType(type) { _, _ -> })
    }

    @JvmSynthetic
    public inline fun <reified T: Any> registerCourierPacket(
        direction: NetworkDirection? = null,
        handler: BiConsumer<T, NetworkEvent.Context> = BiConsumer { _, _ -> }
    ) {
        registerCourierPacket(T::class.java, direction, handler)
    }
}
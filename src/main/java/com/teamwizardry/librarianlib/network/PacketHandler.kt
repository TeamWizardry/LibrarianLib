package com.teamwizardry.librarianlib.network

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

enum class PacketHandler private constructor() {
    INSTANCE;

    var network: SimpleNetworkWrapper
    private var id = 0

    init {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("TeamWizardry")
    }

    @SuppressWarnings("unchecked", "rawtypes")
    fun register(clazz: Class<*>, targetSide: Side) {
        network.registerMessage<PacketBase, IMessage>(PacketBase.Handler::class.java!!, clazz, id++, targetSide)
    }

    companion object {

        fun net(): SimpleNetworkWrapper {
            return INSTANCE.network
        }
    }
}

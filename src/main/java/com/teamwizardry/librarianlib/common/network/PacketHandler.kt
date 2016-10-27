package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.common.util.saving.MessageFieldCache
import net.minecraft.client.Minecraft
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.util.IThreadListener
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

object PacketHandler {

    @JvmField
    val NETWORK: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("TeamWizardry")
    private var id = 0

    @JvmStatic
    fun <T : PacketBase> register(clazz: Class<T>, targetSide: Side) {
        MessageFieldCache.getClassFields(clazz)
        NETWORK.registerMessage<T, PacketBase>(Handler<T>(), clazz, id++, targetSide)
    }

    class Handler<REQ : PacketBase> : IMessageHandler<REQ, PacketBase> {

        override fun onMessage(message: REQ, ctx: MessageContext): PacketBase? {
            val mainThread: IThreadListener
            if (ctx.netHandler is NetHandlerPlayServer)
                mainThread = ctx.serverHandler.playerEntity.worldObj as WorldServer
            else
                mainThread = Minecraft.getMinecraft()
            mainThread.addScheduledTask { message.handle(ctx) }
            return message.reply(ctx)
        }
    }
}

package com.teamwizardry.librarianlib.network

import net.minecraft.client.Minecraft
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.util.IThreadListener
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

abstract class PacketBase : IMessage {

    abstract fun handle(ctx: MessageContext)

    fun reply(ctx: MessageContext): PacketBase? {
        return null
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

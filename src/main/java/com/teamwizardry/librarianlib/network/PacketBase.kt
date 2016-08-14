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

    fun reply(ctx: MessageContext): IMessage? {
        return null
    }

    class Handler : IMessageHandler<PacketBase, IMessage> {

        override fun onMessage(message: PacketBase, ctx: MessageContext): IMessage {
            val mainThread: IThreadListener
            if (ctx.netHandler is NetHandlerPlayServer)
                mainThread = ctx.serverHandler.playerEntity.worldObj as WorldServer
            else
                mainThread = Minecraft.getMinecraft()
            mainThread.addScheduledTask { message.handle(ctx) }
            return message.reply(ctx) // no response in this case
        }
    }
}

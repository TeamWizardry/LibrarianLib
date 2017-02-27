package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.client.util.lambdainterfs.ClientRunnable
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.client.Minecraft
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@PacketRegister(Side.CLIENT)
class PacketSpamlessMessage(@Save var msg: ITextComponent? = null, @Save var id: Int = 0) : PacketBase(), ClientRunnable {
    override fun handle(ctx: MessageContext) = LibrarianLib.PROXY.runIfClient(this)

    @SideOnly(Side.CLIENT)
    override fun runIfClient() = Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessageWithOptionalDeletion(msg, id)
}

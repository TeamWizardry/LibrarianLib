package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.Minecraft
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@PacketRegister(Side.CLIENT)
class PacketSpamlessMessage(@Save var msg: ITextComponent = TextComponentString(""), @Save var id: Int = 0) : PacketBase(), ClientRunnable {
    override fun handle(ctx: MessageContext) = LibrarianLib.PROXY.runIfClient(this)

    @SideOnly(Side.CLIENT)
    override fun runIfClient() = Minecraft.getMinecraft().ingameGUI.chatGUI.printChatMessageWithOptionalDeletion(msg, id)
}

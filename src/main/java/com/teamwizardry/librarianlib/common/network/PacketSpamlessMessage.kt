package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

@PacketRegister(Side.CLIENT)
class PacketSpamlessMessage(@Save var msg: ITextComponent? = null, @Save var id: Int = 0) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val message = msg ?: return
        LibrarianLib.PROXY.sendSpamlessMessage(LibrarianLib.PROXY.getClientPlayer(), message, id)
    }
}

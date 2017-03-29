package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.kotlin.hasNullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNonnullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNullSignature
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.Side

@PacketRegister(Side.CLIENT)
class PacketSynchronization(var tile: TileMod? = null /* Tile is always null on clientside */) : PacketBase() {

    @Save var pos: BlockPos? = null

    init {
        if (tile != null)
            pos = tile!!.pos
    }

    // Buf is always null serverside
    private var buf: ByteBuf? = null

    override fun handle(ctx: MessageContext) {
        if (FMLLaunchHandler.side().isServer) return

        val b = buf

        val tile = Minecraft.getMinecraft().world.getTileEntity(pos)
        if (b == null || tile == null || tile !is TileMod) return

        AbstractSaveHandler.readAutoBytes(tile, b, true)
        tile.readCustomBytes(b)
    }

    override fun readCustomBytes(buf: ByteBuf) {
        if (buf.hasNullSignature()) return
        this.buf = buf.copy()
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        val te = tile
        if (te == null)
            buf.writeNullSignature()
        else {
            buf.writeNonnullSignature()

            AbstractSaveHandler.writeAutoBytes(te, buf, true)
            te.writeCustomBytes(buf, true)
        }
    }
}

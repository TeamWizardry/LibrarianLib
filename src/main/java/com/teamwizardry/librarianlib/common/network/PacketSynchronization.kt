package com.teamwizardry.librarianlib.common.network

import com.teamwizardry.librarianlib.common.base.block.TileMod
import com.teamwizardry.librarianlib.common.util.saving.Save
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.FMLLaunchHandler


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

        val tile = Minecraft.getMinecraft().theWorld.getTileEntity(pos)
        if (b == null || tile == null || tile !is TileMod) return

        tile.readAutoBytes(b)
        tile.readCustomBytes(b)
    }

    override fun readCustomBytes(buf: ByteBuf) {
        if (buf.readBoolean()) return
        this.buf = buf.copy()
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        val te = tile
        if (te == null)
            buf.writeBoolean(true)
        else {
            buf.writeBoolean(false)

            te.writeAutoBytes(buf)
            te.writeCustomBytes(buf)
        }
    }
}

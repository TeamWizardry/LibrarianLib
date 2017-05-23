package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.base.entity.IModEntity
import com.teamwizardry.librarianlib.features.kotlin.hasNullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNonnullSignature
import com.teamwizardry.librarianlib.features.kotlin.writeNullSignature
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.Side

@PacketRegister(Side.CLIENT)
class PacketEntitySynchronization(@Save var entityId: Int = -1, val entity: IModEntity? = null) : PacketBase() {

    @Save var pos: BlockPos? = null

    // Buf is always null serverside
    private var buf: ByteBuf? = null

    override fun handle(ctx: MessageContext) {
        if (FMLLaunchHandler.side().isServer) return

        val b = buf

        val entity = Minecraft.getMinecraft().world.getEntityByID(entityId)
        if (b == null || entity == null || entity !is IModEntity) return

        AbstractSaveHandler.readAutoBytes(entity, b, true)
        entity.readCustomBytes(b)
        b.release()
    }

    override fun readCustomBytes(buf: ByteBuf) {
        this.buf = buf.copy()
    }

    override fun writeCustomBytes(buf: ByteBuf) {
        entity?.let {
            AbstractSaveHandler.writeAutoBytes(entity, buf, true)
            entity.writeCustomBytes(buf)
        }
    }
}

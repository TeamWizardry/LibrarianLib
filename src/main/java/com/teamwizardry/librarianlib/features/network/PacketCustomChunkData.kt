package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.chunkdata.ChunkData
import com.teamwizardry.librarianlib.features.kotlin.missingno
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * Syncs custom chunk data, some more complex chunk data should be synced incrementally however
 */
@PacketRegister(Side.CLIENT)
class PacketCustomChunkData(@Save var pos: ChunkPos, @Save var name: ResourceLocation, data: ChunkData? = null) : PacketBase() {
    constructor() : this(ChunkPos(0, 0), missingno, null)

    @Save var dataBuf: ByteBuf = Unpooled.buffer()

    init {
        data?.writeToBytes(dataBuf)
    }

    override fun handle(ctx: MessageContext) {
        ChunkData.get(Minecraft.getMinecraft().world, pos, name)?.readFromBytes(dataBuf)
    }
}

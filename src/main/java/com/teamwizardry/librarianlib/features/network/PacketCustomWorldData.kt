package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.worlddata.WorldData
import com.teamwizardry.librarianlib.features.kotlin.missingno
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * Syncs custom world data, some more complex chunk data should be synced incrementally.
 * However this is the responsibility of the mods to implement.
 */
@PacketRegister(Side.CLIENT)
class PacketCustomWorldData(@Save var name: ResourceLocation, data: WorldData? = null) : PacketAbstractUpdate(name) {
    constructor() : this(missingno, null)

    @Save
    var dataBuf: ByteBuf = Unpooled.buffer()

    init {
        data?.writeToBytes(dataBuf)
    }

    override fun handle(ctx: MessageContext) {
        WorldData.get(Minecraft.getMinecraft().world, name)?.readFromBytes(dataBuf)
    }
}

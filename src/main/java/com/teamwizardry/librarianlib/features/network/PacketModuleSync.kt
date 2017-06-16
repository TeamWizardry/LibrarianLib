package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.base.block.TileMod
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.Side

@PacketRegister(Side.CLIENT)
class PacketModuleSync(@Save var data: NBTTagCompound = NBTTagCompound(), @Save val name: String = "", @Save val pos: BlockPos = BlockPos.ORIGIN) : PacketBase() {

    override fun handle(ctx: MessageContext) {
        (LibrarianLib.PROXY.getClientPlayer().world.getTileEntity(pos) as? TileMod)?.let {
            it.readSingleModuleNBT(name, data)
        }
    }
}

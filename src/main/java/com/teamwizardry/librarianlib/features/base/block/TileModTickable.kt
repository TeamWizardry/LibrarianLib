package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketTileSynchronization
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileModTickable : TileMod(), ITickable {
    abstract fun tick()

    override final fun update() {
        modules.forEach { it.value.onUpdate(this) }
        tick()
    }
}

package com.teamwizardry.librarianlib.foundation.tileentity

import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.SimpleSerializer
import com.teamwizardry.librarianlib.prism.Sync
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.util.Constants

public abstract class BaseTileEntity(tileEntityTypeIn: TileEntityType<*>): TileEntity(tileEntityTypeIn) {
    public constructor(tileEntityTypeIn: LazyTileEntityType<*>) : this(tileEntityTypeIn.get())

    private val serializer = SimpleSerializer.get(this.javaClass)

    /**
     * Triggers a block update to send updated tile information to clients
     */
    public fun notifyStateChange() {
        world?.notifyBlockUpdate(pos, blockState, blockState, Constants.BlockFlags.BLOCK_UPDATE)
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        super.write(compound)
        compound.put("ll", serializer.createTag(this, Save::class.java))
        return compound
    }

    override fun read(compound: CompoundNBT) {
        super.read(compound)
        serializer.applyTag(compound.getCompound("ll"), this, Save::class.java)
    }

    override fun getUpdateTag(): CompoundNBT {
        val tag = super.getUpdateTag()
        tag.put("ll", serializer.createTag(this, Sync::class.java))
        return tag
    }

    override fun handleUpdateTag(tag: CompoundNBT) {
        super.handleUpdateTag(tag)
        serializer.applyTag(tag.getCompound("ll"), this, Sync::class.java)
    }

    override fun getUpdatePacket(): SUpdateTileEntityPacket? {
        val tag = serializer.createTag(this, Sync::class.java)
        return SUpdateTileEntityPacket(getPos(), -1, tag)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SUpdateTileEntityPacket) {
        val tag = pkt.nbtCompound
        serializer.applyTag(tag, this, Sync::class.java)
    }
}
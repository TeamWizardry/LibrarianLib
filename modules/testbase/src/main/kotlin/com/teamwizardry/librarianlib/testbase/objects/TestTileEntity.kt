package com.teamwizardry.librarianlib.testbase.objects

import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.SimpleSerializer
import com.teamwizardry.librarianlib.prism.Sync
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityType

public abstract class TestTileEntity(tileEntityTypeIn: TileEntityType<*>): TileEntity(tileEntityTypeIn) {
    private val serializer = SimpleSerializer.get(this.javaClass)

    override fun write(compound: CompoundNBT): CompoundNBT {
        super.write(compound)
        compound.put("ll", serializer.createTag(this, Save::class.java))
        return compound
    }

    override fun read(state: BlockState, compound: CompoundNBT) {
        super.read(state, compound)
        serializer.applyTag(compound.getCompound("ll"), this, Save::class.java)
    }

    override fun getUpdateTag(): CompoundNBT {
        val tag = super.getUpdateTag()
        tag.put("ll", serializer.createTag(this, Sync::class.java))
        return tag
    }

    override fun handleUpdateTag(state: BlockState?, tag: CompoundNBT) {
        super.handleUpdateTag(state, tag)
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

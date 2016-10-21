package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.LibrarianLib
import com.teamwizardry.librarianlib.common.core.ConfigHandler
import com.teamwizardry.librarianlib.common.util.AutomaticTileSavingHandler
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.INBTSerializable

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileMod : TileEntity() {
    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        writeCustomNBT(compound)
        super.writeToNBT(compound)
        if (ConfigHandler.autoSaveTEs) {
            javaClass.declaredFields.filter { it in AutomaticTileSavingHandler.fieldMap.keys }.forEach {
                it.isAccessible = true
                val get = it.get(this)
                val type = it.type
                when (type) {
                    String::class.java -> compound.setString(it.name, get as String)
                    Int::class.javaPrimitiveType!! -> compound.setInteger(it.name, get as Int)
                    Boolean::class.java -> compound.setBoolean(it.name, get as Boolean)
                    Byte::class.java -> compound.setByte(it.name, get as Byte)
                    Float::class.java -> compound.setFloat(it.name, get as Float)
                    Double::class.java -> compound.setDouble(it.name, get as Double)
                    Long::class.java -> compound.setLong(it.name, get as Long)
                    NBTBase::class.java -> compound.setTag(it.name, get as NBTBase)
                    INBTSerializable::class.java -> compound.setTag(it.name, (get as INBTSerializable<NBTTagCompound>).serializeNBT())
                    ItemStack::class.java -> {
                        val tag = NBTTagCompound()
                        (get as ItemStack?)?.writeToNBT(tag)
                        compound.setTag(it.name, tag)
                    }
                    else -> {
                        if (AutomaticTileSavingHandler.fieldMap[it]?.first != null) {
                            val tag = NBTTagCompound()
                            val clazz = AutomaticTileSavingHandler.fieldMap[it]
                                    ?.first
                            val instance = clazz?.newInstance()
                            AutomaticTileSavingHandler.fieldMap[it]
                                    ?.first
                                    ?.getDeclaredMethod("writeToNBT", it.type, NBTTagCompound::class.java, String::class.java)
                                    ?.invoke(instance, get, tag, it.name)
                            compound.setTag(it.name, tag)
                        } else throw IllegalArgumentException("Invalid field " + it.name)
                    }
                }
                if(LibrarianLib.DEV_ENVIRONMENT) println("Saved ${it.name}")
            }
        }

        return compound
    }

    @Suppress("UNCHECKED_CAST")
    override fun readFromNBT(compound: NBTTagCompound) {
        readCustomNBT(compound)
        super.readFromNBT(compound)
        if (ConfigHandler.autoSaveTEs) {
            javaClass.declaredFields.filter { it in AutomaticTileSavingHandler.fieldMap.keys }.forEach {
                val type = it.type
                it.isAccessible = true
                when (type) {
                    String::class.java -> it.set(this, compound.getString(it.name))
                    Int::class.javaPrimitiveType!! -> it.set(this, compound.getInteger(it.name))
                    Boolean::class.java -> it.set(this, compound.getBoolean(it.name))
                    Byte::class.java -> it.set(this, compound.getByte(it.name))
                    Float::class.java -> it.set(this, compound.getFloat(it.name))
                    Double::class.java -> it.set(this, compound.getDouble(it.name))
                    Long::class.java -> it.set(this, compound.getLong(it.name))
                    NBTBase::class.java -> it.set(this, compound.getTag(it.name))
                    INBTSerializable::class.java -> (it.get(this) as INBTSerializable<NBTTagCompound>).deserializeNBT(compound.getCompoundTag(it.name))
                    ItemStack::class.java -> {
                        val tag = compound.getCompoundTag(it.name)
                        it.set(this, ItemStack.loadItemStackFromNBT(tag))
                    }
                    else -> {
                        if (AutomaticTileSavingHandler.fieldMap[it]?.first != null) {
                            val tag = compound.getCompoundTag(it.name)
                            val clazz = AutomaticTileSavingHandler.fieldMap[it]
                                    ?.first
                            val instance = clazz?.newInstance()
                            it.set(this, AutomaticTileSavingHandler.fieldMap[it]
                                    ?.first
                                    ?.getDeclaredMethod("readFromNBT", NBTTagCompound::class.java, String::class.java)
                                    ?.invoke(instance, tag, it.name))
                        } else throw IllegalArgumentException("Invalid field " + it.name)
                    }
                }
                if(LibrarianLib.DEV_ENVIRONMENT) println("Read ${it.name} (${it.get(this)})")
            }
        }
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(super.getUpdateTag())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, -999, updateTag)
    }

    open fun writeCustomNBT(cmp: NBTTagCompound) {
        // NO-OP
    }

    open fun readCustomNBT(cmp: NBTTagCompound) {
        // NO-OP
    }

    override fun markDirty() {
        super.markDirty()
        if (!worldObj.isRemote)
            dispatchTileToNearbyPlayers()
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        readCustomNBT(packet.nbtCompound)
    }

    open fun dispatchTileToNearbyPlayers() {
        if (worldObj is WorldServer) {
            val ws: WorldServer = worldObj as WorldServer
            val packet: SPacketUpdateTileEntity = updatePacket

            for (player in ws.playerEntities) {
                val playerMP = player as EntityPlayerMP
                if (playerMP.getDistanceSq(getPos()) < 64 * 64
                        && ws.playerChunkMap.isPlayerWatchingChunk(playerMP, pos.x shr 4, pos.z shr 4)) {
                    playerMP.connection.sendPacket(packet)
                }
            }
        }
    }
}

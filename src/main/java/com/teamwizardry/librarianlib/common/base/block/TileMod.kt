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

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileMod : TileEntity() {
    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(par1nbtTagCompound: NBTTagCompound): NBTTagCompound {
        writeCustomNBT(par1nbtTagCompound)
        super.writeToNBT(par1nbtTagCompound)
        if (ConfigHandler.autoSaveTEs) {
            javaClass.declaredFields.filter { it in AutomaticTileSavingHandler.fieldMap.keys }.forEach {
                it.isAccessible = true
                val get = it.get(this)
                val type = it.type
                when (type) {
                    String::class.java -> par1nbtTagCompound.setString(it.name, get as String)
                    Int::class.javaPrimitiveType!! -> par1nbtTagCompound.setInteger(it.name, get as Int)
                    Boolean::class.java -> par1nbtTagCompound.setBoolean(it.name, get as Boolean)
                    Byte::class.java -> par1nbtTagCompound.setByte(it.name, get as Byte)
                    Float::class.java -> par1nbtTagCompound.setFloat(it.name, get as Float)
                    Double::class.java -> par1nbtTagCompound.setDouble(it.name, get as Double)
                    Long::class.java -> par1nbtTagCompound.setLong(it.name, get as Long)
                    NBTBase::class.java -> par1nbtTagCompound.setTag(it.name, get as NBTBase)
                    ItemStack::class.java -> {
                        val tag = NBTTagCompound()
                        (get as ItemStack?)?.writeToNBT(tag)
                        par1nbtTagCompound.setTag(it.name, tag)
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
                            par1nbtTagCompound.setTag(it.name, tag)
                        } else throw IllegalArgumentException("Invalid field " + it.name)
                    }
                }
                if(LibrarianLib.DEV_ENVIRONMENT) println("Saved ${it.name}")
            }
        }

        return par1nbtTagCompound
    }

    override fun readFromNBT(par1nbtTagCompound: NBTTagCompound) {
        readCustomNBT(par1nbtTagCompound)
        super.readFromNBT(par1nbtTagCompound)
        if (ConfigHandler.autoSaveTEs) {
            javaClass.declaredFields.filter { it in AutomaticTileSavingHandler.fieldMap.keys }.forEach {
                val type = it.type
                it.isAccessible = true
                when (type) {
                    String::class.java -> it.set(this, par1nbtTagCompound.getString(it.name))
                    Int::class.javaPrimitiveType!! -> it.set(this, par1nbtTagCompound.getInteger(it.name))
                    Boolean::class.java -> it.set(this, par1nbtTagCompound.getBoolean(it.name))
                    Byte::class.java -> it.set(this, par1nbtTagCompound.getByte(it.name))
                    Float::class.java -> it.set(this, par1nbtTagCompound.getFloat(it.name))
                    Double::class.java -> it.set(this, par1nbtTagCompound.getDouble(it.name))
                    Long::class.java -> it.set(this, par1nbtTagCompound.getLong(it.name))
                    NBTBase::class.java -> it.set(this, par1nbtTagCompound.getTag(it.name))
                    ItemStack::class.java -> {
                        val tag = par1nbtTagCompound.getCompoundTag(it.name)
                        it.set(this, ItemStack.loadItemStackFromNBT(tag))
                    }
                    else -> {
                        if (AutomaticTileSavingHandler.fieldMap[it]?.first != null) {
                            val tag = par1nbtTagCompound.getCompoundTag(it.name)
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
package com.teamwizardry.librarianlib.features.base.block.tile

import com.teamwizardry.librarianlib.features.base.block.tile.module.ITileModule
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.saving.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.filter
import kotlin.collections.filterIsInstance
import kotlin.collections.forEach
import kotlin.collections.iterator
import kotlin.collections.map
import kotlin.collections.toTypedArray

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
@com.teamwizardry.librarianlib.features.saving.SaveInPlace
abstract class TileMod : net.minecraft.tileentity.TileEntity() {

    protected val modules = kotlin.collections.mutableMapOf<String, ITileModule>()

    protected fun initModule(name: String, module: com.teamwizardry.librarianlib.features.base.block.tile.module.ITileModule) = modules.put(name, module)

    fun onBreak() = modules.forEach { it.value.onBreak(this) }

    override fun onLoad() {
        createModules()
        modules.forEach { it.value.onLoad(this) }
    }

    fun syncModule(module: com.teamwizardry.librarianlib.features.base.block.tile.module.ITileModule) {
        val name = modules.entries.firstOrNull { it.value === module }?.key ?: return
        val ws = world as? net.minecraft.world.WorldServer ?: return
        ws.playerEntities
                .filterIsInstance<net.minecraft.entity.player.EntityPlayerMP>()
                .filter { it.getDistanceSq(getPos()) < 64 * 64 && ws.playerChunkMap.isPlayerWatchingChunk(it, pos.x shr 4, pos.z shr 4) }
                .forEach { com.teamwizardry.librarianlib.features.network.PacketHandler.NETWORK.sendTo(com.teamwizardry.librarianlib.features.network.PacketModuleSync(module.writeToNBT(true), name, pos), it) }
    }

    private var modulesSetUp = false

    fun createModules() {
        if (modulesSetUp) return
        modulesSetUp = true

        for ((name, field) in com.teamwizardry.librarianlib.features.saving.SavingFieldCache.getClassFields(FieldType.create(javaClass))) {
            if (field.meta.hasFlag(com.teamwizardry.librarianlib.features.saving.SavingFieldFlag.MODULE)) {
                @Suppress("LeakingThis")
                val module = field.getter(this) as? com.teamwizardry.librarianlib.features.base.block.tile.module.ITileModule
                module?.let { initModule(name, it) }
            }
        }

    }

    fun onClicked(player: net.minecraft.entity.player.EntityPlayer, hand: net.minecraft.util.EnumHand, side: net.minecraft.util.EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = modules
            .map { it.value.onClicked(this, player, hand, side, hitX, hitY, hitZ) }
            .any { it }

    fun writeModuleNBT(sync: Boolean): net.minecraft.nbt.NBTTagCompound {
        createModules()
        return com.teamwizardry.librarianlib.features.kotlin.nbt {
            comp(
                    *modules.map {
                        it.key to it.value.writeToNBT(sync)
                    }.toTypedArray()
            )
        } as net.minecraft.nbt.NBTTagCompound
    }

    fun readModuleNBT(nbt: net.minecraft.nbt.NBTTagCompound) {
        createModules()
        nbt.forEach { key, value ->
            if (value is net.minecraft.nbt.NBTTagCompound) {
                readSingleModuleNBT(key, value)
            }
        }
    }

    fun readSingleModuleNBT(key: String, value: net.minecraft.nbt.NBTTagCompound) {
        val module = modules[key]
        module?.readFromNBT(value)
    }

    /**
     * Using fast synchronization is quicker and less expensive than the NBT packet default.
     * However, it requires you to register both ByteBuf serializers and NBT serializers for custom types.
     * If you for some reason can only use NBT serializers, turn this property to false.
     */
    open val useFastSync: Boolean
        get() = true

    override fun shouldRefresh(world: net.minecraft.world.World, pos: net.minecraft.util.math.BlockPos, oldState: net.minecraft.block.state.IBlockState, newState: net.minecraft.block.state.IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(compound: net.minecraft.nbt.NBTTagCompound): net.minecraft.nbt.NBTTagCompound {
        val customTag = net.minecraft.nbt.NBTTagCompound()
        writeCustomNBT(customTag, false)
        compound.setTag("custom", customTag)
        compound.setTag("auto", com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.writeAutoNBT(this, false))
        compound.setTag("module", writeModuleNBT(false))
        compound.setInteger("_v", 2)
        super.writeToNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: net.minecraft.nbt.NBTTagCompound) {
        readCustomNBT(compound.getCompoundTag("custom"))
        readModuleNBT(compound.getCompoundTag("module"))
        if (!compound.hasKey("_v"))
            com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.readAutoNBT(this, compound, false)
        else {
            when (compound.getInteger("_v")) {
                2 -> com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.readAutoNBT(this, compound.getTag("auto"), false)
            }
        }
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): net.minecraft.nbt.NBTTagCompound {
        val compound = super.getUpdateTag()
        val customTag = net.minecraft.nbt.NBTTagCompound()
        writeCustomNBT(customTag, true)
        compound.setTag("custom", customTag)
        compound.setTag("auto", com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.writeAutoNBT(this, true))
        compound.setTag("module", writeModuleNBT(true))
        super.writeToNBT(compound)

        return compound
    }

    override fun getUpdatePacket(): net.minecraft.network.play.server.SPacketUpdateTileEntity {
        return net.minecraft.network.play.server.SPacketUpdateTileEntity(pos, -999, updateTag)
    }

    /**
     * Override this function to store special data not stored in @Save fields in NBT.
     * If [useFastSync] is false, this will also determine whether it gets sent to clientside.
     *
     * [sync] implies that this is being used to send to clientside.
     */
    open fun writeCustomNBT(cmp: net.minecraft.nbt.NBTTagCompound, sync: Boolean) {
        // NO-OP
    }

    /**
     * Override this function to read special data not stored in @Save fields from NBT.
     * If [useFastSync] is false, this will also determine what the client receives.
     */
    open fun readCustomNBT(cmp: net.minecraft.nbt.NBTTagCompound) {
        // NO-OP
    }

    /**
     * Override this function to write special data not stored in @Save fields to bytes.
     * If [useFastSync] is false, this function is never called.
     *
     * [sync] implies that this is being used to send to clientside.
     */
    open fun writeCustomBytes(buf: io.netty.buffer.ByteBuf, sync: Boolean) {
        // NO-OP
    }

    /**
     * Override this function to read special data not stored in @Save fields from bytes.
     * If [useFastSync] is false, this function is never called.
     */
    open fun readCustomBytes(buf: io.netty.buffer.ByteBuf) {
        // NO-OP
    }

    /**
     * Tell the server and nearby clients that this tile has changed.
     */
    override fun markDirty() {
        super.markDirty()
        if (!world.isRemote)
            dispatchTileToNearbyPlayers()
    }

    override fun onDataPacket(net: net.minecraft.network.NetworkManager, packet: net.minecraft.network.play.server.SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        handleUpdateTag(packet.nbtCompound)
    }

    override fun handleUpdateTag(tag: net.minecraft.nbt.NBTTagCompound) {
        readCustomNBT(tag.getCompoundTag("custom"))
        com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.readAutoNBT(this, tag.getTag("auto"), true)
        readModuleNBT(tag.getCompoundTag("module"))
    }

    /**
     * Dispatch tile data to nearby players. This will sync data to client side.
     */
    open fun dispatchTileToNearbyPlayers() {
        if (world is net.minecraft.world.WorldServer) {
            val ws: net.minecraft.world.WorldServer = world as net.minecraft.world.WorldServer

            ws.playerEntities
                    .filterIsInstance<net.minecraft.entity.player.EntityPlayerMP>()
                    .filter { it.getDistanceSq(getPos()) < 64 * 64 && ws.playerChunkMap.isPlayerWatchingChunk(it, pos.x shr 4, pos.z shr 4) }
                    .forEach { sendUpdatePacket(it) }
        }
    }

    /**
     * The specific implementation for an update packet.
     * By default, controlled by [useFastSync], it will send the vanilla packet or the LibLib sync packet.
     */
    open fun sendUpdatePacket(player: net.minecraft.entity.player.EntityPlayerMP) {
        if (useFastSync)
            com.teamwizardry.librarianlib.features.network.PacketHandler.NETWORK.sendTo(com.teamwizardry.librarianlib.features.network.PacketTileSynchronization(this), player)
        else
            player.connection.sendPacket(updatePacket)
    }

    override fun <T : Any> getCapability(capability: net.minecraftforge.common.capabilities.Capability<T>, facing: net.minecraft.util.EnumFacing?): T? {
        return modules.values.mapNotNull { it.getCapability(capability, facing) }.firstOrNull() ?:
                com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.getCapability(this, capability, facing) ?:
                super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: net.minecraftforge.common.capabilities.Capability<*>, facing: net.minecraft.util.EnumFacing?): Boolean {
        return modules.values.any { it.hasCapability(capability, facing) } ||
                com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler.hasCapability(this, capability, facing) ||
                super.hasCapability(capability, facing)
    }
}

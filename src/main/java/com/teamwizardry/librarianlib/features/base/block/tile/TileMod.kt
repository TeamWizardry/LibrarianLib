package com.teamwizardry.librarianlib.features.base.block.tile

import com.teamwizardry.librarianlib.features.base.block.tile.module.ITileModule
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.kotlin.forEach
import com.teamwizardry.librarianlib.features.kotlin.nbt
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.PacketModuleSync
import com.teamwizardry.librarianlib.features.network.PacketTileSynchronization
import com.teamwizardry.librarianlib.features.network.TargetWatchingBlock
import com.teamwizardry.librarianlib.features.saving.*
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
@SaveInPlace
abstract class TileMod : TileEntity() {

    protected val modules = mutableMapOf<String, ITileModule>()

    protected fun initModule(name: String, module: ITileModule) = modules.put(name, module)

    fun onBreak() = modules.forEach { it.value.onBreak(this) }

    fun hasComparatorOverride(): Boolean {
        createModules()
        return modules.any { it.value.hasComparatorOutput() }
    }

    fun getComparatorOverride() = ModuleInventory.getPowerLevel(modules
            .filter { it.value.hasComparatorOutput() }
            .let { it.map { it.value.getComparatorOutput(this) }.sum() / it.size })

    override fun onLoad() {
        createModules()
        modules.forEach { it.value.onLoad(this) }
    }

    fun syncModule(module: ITileModule) {
        val name = modules.entries.firstOrNull { it.value === module }?.key ?: return
        PacketHandler.CHANNEL.update(TargetWatchingBlock(world, pos), PacketModuleSync(module.writeToNBT(true), name, pos))
    }

    private var modulesSetUp = false

    fun createModules() {
        if (modulesSetUp) return
        modulesSetUp = true

        val state = world.getBlockState(pos)
        val block = state.block
        if (block is BlockModContainer && block.hasComparatorInputOverride == null)
            block.hasComparatorInputOverride = hasComparatorOverride()

        for ((name, field) in SavingFieldCache.getClassFields(FieldType.create(javaClass, null))) {
            if (field.meta.hasFlag(SavingFieldFlag.MODULE)) {
                @Suppress("LeakingThis")
                val module = field.getter(this) as? ITileModule
                module?.let { initModule(name, it) }
            }
        }
    }

    fun onClicked(player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = modules
            .map { it.value.onClicked(this, player, hand, side, hitX, hitY, hitZ) }
            .any { it }

    fun writeModuleNBT(sync: Boolean): NBTTagCompound {
        createModules()
        return nbt {
            comp(
                    *modules.map {
                        it.key to it.value.writeToNBT(sync)
                    }.toTypedArray()
            )
        } as NBTTagCompound
    }

    fun readModuleNBT(nbt: NBTTagCompound) {
        createModules()
        nbt.forEach { key, value ->
            if (value is NBTTagCompound) {
                readSingleModuleNBT(key, value)
            }
        }
    }

    fun readSingleModuleNBT(key: String, value: NBTTagCompound) {
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

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val customTag = NBTTagCompound()
        writeCustomNBT(customTag, false)
        compound.setTag("custom", customTag)
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, false))
        compound.setTag("module", writeModuleNBT(false))
        compound.setInteger("_v", 2)
        super.writeToNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        readCustomNBT(compound.getCompoundTag("custom"))
        readModuleNBT(compound.getCompoundTag("module"))
        if (!compound.hasKey("_v"))
            AbstractSaveHandler.readAutoNBT(this, compound, false)
        else {
            when (compound.getInteger("_v")) {
                2 -> AbstractSaveHandler.readAutoNBT(this, compound.getTag("auto"), false)
            }
        }
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        val compound = super.getUpdateTag()
        val customTag = NBTTagCompound()
        writeCustomNBT(customTag, true)
        compound.setTag("custom", customTag)
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, true))
        compound.setTag("module", writeModuleNBT(true))
        super.writeToNBT(compound)

        return compound
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, -999, updateTag)
    }

    /**
     * Override this function to store special data not stored in @Save fields in NBT.
     * If [useFastSync] is false, this will also determine whether it gets sent to clientside.
     *
     * [sync] implies that this is being used to send to clientside.
     */
    open fun writeCustomNBT(cmp: NBTTagCompound, sync: Boolean) {
        // NO-OP
    }

    /**
     * Override this function to read special data not stored in @Save fields from NBT.
     * If [useFastSync] is false, this will also determine what the client receives.
     */
    open fun readCustomNBT(cmp: NBTTagCompound) {
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

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        handleUpdateTag(packet.nbtCompound)
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        readCustomNBT(tag.getCompoundTag("custom"))
        AbstractSaveHandler.readAutoNBT(this, tag.getTag("auto"), true)
        readModuleNBT(tag.getCompoundTag("module"))
    }

    /**
     * Dispatch tile data to nearby players. This will sync data to client side.
     */
    open fun dispatchTileToNearbyPlayers() {
        if (world is WorldServer) {
            val ws: WorldServer = world as WorldServer

            if(useFastSync) {
                PacketHandler.CHANNEL.update(TargetWatchingBlock(world, pos), PacketTileSynchronization(this))
            } else {
                ws.playerEntities
                        .filterIsInstance<EntityPlayerMP>()
                        .filter { it.getDistanceSq(getPos()) < 64 * 64 && ws.playerChunkMap.isPlayerWatchingChunk(it, pos.x shr 4, pos.z shr 4) }
                        .forEach { sendUpdatePacket(it) }
            }
        }
    }

    /**
     * The specific implementation for an individual update packet.
     *
     * Unused if [useFastSync]. By default it will send the vanilla packet or the LibLib sync packet.
     */
    open fun sendUpdatePacket(player: EntityPlayerMP) {
        player.connection.sendPacket(updatePacket)
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return modules.values.mapNotNull { it.getCapability(capability, facing) }.firstOrNull() ?:
                AbstractSaveHandler.getCapability(this, capability, facing) ?:
                super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return modules.values.any { it.hasCapability(capability, facing) } ||
                AbstractSaveHandler.hasCapability(this, capability, facing) ||
                super.hasCapability(capability, facing)
    }
}

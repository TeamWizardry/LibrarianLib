package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.util.VariantHelper
import com.teamwizardry.librarianlib.common.util.saving.SavingFieldCache
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoAccessor
import mcjty.theoneprobe.api.ProbeMode
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import mcp.mobius.waila.api.IWailaDataProvider
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
@Optional.InterfaceList(
        Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe", striprefs = true),
        Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila", striprefs = true))
open class BlockMod(name: String, materialIn: Material, color: MapColor, vararg variants: String) : Block(materialIn, color), IModBlock, IProbeInfoAccessor, IWailaDataProvider {

    constructor(name: String, materialIn: Material, vararg variants: String) : this(name, materialIn, materialIn.materialMapColor, *variants)

    override val variants: Array<out String>

    override val bareName: String = name
    val modId: String

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        modId = Loader.instance().activeModContainer().modId
        this.variants = VariantHelper.beginSetupBlock(name, variants)
        VariantHelper.finishSetupBlock(this, name, itemForm, creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }
    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]


    // Compat //

    /**
     * Override this to add probe information. Only called when The One Probe exists.
     */
    open fun addProbeInformation(info: ProbeInfoWrapper?, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }

    /**
     * Override this to add probe information. Only called when WAILA exists.
     */
    open fun addWailaInfo(info: MutableList<String>, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }

    /**
     * Override this to add extra NBT to WAILA data.
     */
    open fun writeToNbtForWaila(te: TileEntity, nbtTagCompound: NBTTagCompound, player: EntityPlayer) {
        if(te is TileMod) te.writeToNBT(nbtTagCompound)
    }

    /**
     * Override this to have a
     */
    open fun getStackForWaila(player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) =
            ItemStack(this, 1, damageDropped(blockState))

    /**
     * Override this to add simple text information to both WAILA and The One Probe.
     */
    open fun addHudInformation(wrapper: TopAndWailaWrapper, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }

    // Optional Methods //

    /**
     * Marked final due to overriding errors with it not existing. Use [addWailaInfo] instead.
     */
    @Optional.Method(modid = "Waila")
    override final fun getWailaBody(itemStack: ItemStack?, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?): MutableList<String>? {
        val wrapper = TopAndWailaWrapper()
        addWailaInfo(currenttip, accessor.player, accessor.world, accessor.blockState, accessor.position)
        addHudInformation(wrapper, accessor.player, accessor.world, accessor.blockState, accessor.position)
        val te = accessor.tileEntity
        if(te is TileMod && te.automaticallyAddFieldsToWaila) {
            SavingFieldCache.getClassFields(te.javaClass).forEach {
                if(it.value.wailaName != null) {
                    if(it.value.wailaName == "thisIsADefaultName")
                        wrapper.list.add("${it.key}: ${it.value.getter(te)}")
                    else wrapper.list.add("${it.value.wailaName}: ${it.value.getter(te)}")
                }
            }
        }
        return wrapper.list
    }

    /**
     * Marked final due to overriding errors with it not existing. Use [getStackForWaila] instead.
     */
    @Optional.Method(modid = "Waila")
    override final fun getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler?): ItemStack? {
        return getStackForWaila(accessor.player, accessor.world, accessor.blockState, accessor.position)
    }

    /**
     * Marked final due to overriding errors with it not existing. Use [addWailaInfo] instead.
     */
    @Optional.Method(modid = "Waila")
    override final fun getWailaTail(itemStack: ItemStack?, currenttip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String>? {
        return currenttip
    }

    /**
     * Marked final due to overriding errors with it not existing. Use [writeToNbtForWaila] instead.
     */
    @Optional.Method(modid = "Waila")
    override fun getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, pos: BlockPos?): NBTTagCompound? {
        writeToNbtForWaila(te, tag, player)
        return tag
    }

    @Optional.Method(modid = "Waila")
    override final fun getWailaHead(itemStack: ItemStack?, currenttip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String>? {
        return currenttip
    }

    /**
     * Marked final due to overriding errors with it not existing. Use [addProbeInformation] instead.
     */
    @Optional.Method(modid = "theoneprobe")
    override final fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        val topWrapper = ProbeInfoWrapper(mode, probeInfo, data)
        if(!isWailaLoaded) {
            val wrapper = TopAndWailaWrapper()
            addHudInformation(wrapper, player, world, blockState, data.pos)
            wrapper.list.forEach { probeInfo.text(it) }
        }

        addProbeInformation(topWrapper, player, world, blockState, data.pos)
    }

    companion object {
        @JvmField
        val ALL_BLOCKS = mutableListOf<Block>()

        val isTopLoaded: Boolean by lazy { Loader.isModLoaded("theoneprobe") }
        val isWailaLoaded: Boolean by lazy { Loader.isModLoaded("Waila") }
    }
}

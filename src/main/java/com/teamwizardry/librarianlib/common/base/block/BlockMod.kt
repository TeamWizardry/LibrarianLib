package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.util.VariantHelper
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
@Optional.InterfaceList(Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe", striprefs = true), Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila", striprefs = true))
open class BlockMod(name: String, materialIn: Material, color: MapColor, vararg variants: String) : Block(materialIn, color), IModBlock, IProbeInfoAccessor, IWailaDataProvider {

    @Optional.Method(modid = "Waila")
    override final fun getWailaBody(itemStack: ItemStack?, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler?): MutableList<String>? {
        wrapper.list = mutableListOf()
        addHudInformation(wrapper, accessor.player, accessor.world, accessor.blockState, accessor.position)
        return wrapper.list
    }

    @Optional.Method(modid = "Waila")
    override final fun getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler?): ItemStack? {
        return getStackForWaila(accessor)
    }

    open fun getStackForWaila(accessor: IWailaDataAccessor) = ItemStack(this, 1, accessor.metadata)

    @Optional.Method(modid = "Waila")
    override final fun getWailaTail(itemStack: ItemStack?, currenttip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String>? {
        //noop
        return currenttip
    }

    @Optional.Method(modid = "Waila")
    override fun getNBTData(player: EntityPlayerMP?, te: TileEntity?, tag: NBTTagCompound?, world: World?, pos: BlockPos?): NBTTagCompound? {
        //noop FOR NOW!
        return tag
    }

    @Optional.Method(modid = "Waila")
    override final fun getWailaHead(itemStack: ItemStack?, currenttip: MutableList<String>?, accessor: IWailaDataAccessor?, config: IWailaConfigHandler?): MutableList<String>? {
        //noop
        return currenttip
    }

    constructor(name: String, materialIn: Material, vararg variants: String) : this(name, materialIn, materialIn.materialMapColor, *variants)

    override val variants: Array<out String>

    override val bareName: String = name
    val modId: String

    companion object {
        @JvmField
        internal val blocks: MutableList<BlockMod> = mutableListOf()
    }

    val itemForm: ItemBlock? by lazy { createItemForm() }

    val isTopLoaded: Boolean by lazy { Loader.isModLoaded("theoneprobe") }
    val isWailaLoaded: Boolean by lazy { Loader.isModLoaded("theoneprobe") }

    init {
        modId = Loader.instance().activeModContainer().modId
        this.variants = VariantHelper.beginSetupBlock(name, variants)
        blocks.add(this)
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
     * Marked final due to overriding errors with it not existing. Use [addProbeInformation] instead.
     */
    @Optional.Method(modid = "theoneprobe")
    override final fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
        topWrapper = ProbeInfoWrapper(mode, probeInfo, data)
        if(!isWailaLoaded) {
            wrapper.list = mutableListOf()
            addHudInformation(wrapper, player, world, blockState, data.pos)
        }
        for(string in wrapper.list) probeInfo.text(string)
    }

    /**
     * Override this to add probe information. Only called when The One Probe exists.
     */
    open fun addProbeInformation(info: ProbeInfoWrapper, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }

    val wrapper = TopAndWailaWrapper()
    var topWrapper: ProbeInfoWrapper? = null
    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    /**
     * Override this to add probe information. Only called when WAILA exists.
     */
    open fun addWailaInfo(info: MutableList<String>, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }

    /**
     * TOP and Waila information! What a time to live in.
     */
    open fun addHudInformation(wrapper: TopAndWailaWrapper, player: EntityPlayer, world: World, blockState: IBlockState, pos: BlockPos) {
        //NO-OP
    }
}

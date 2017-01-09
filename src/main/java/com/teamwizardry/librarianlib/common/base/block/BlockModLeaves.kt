package com.teamwizardry.librarianlib.common.base.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.IShearable
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import java.util.*


@Suppress("LeakingThis")
abstract class BlockModLeaves(name: String, vararg variants: String) : BlockMod(name, Material.LEAVES, *variants), IShearable {
    companion object : IFuelHandler {
        override fun getBurnTime(fuel: ItemStack)
                = if (fuel.item is ItemBlock && (fuel.item as ItemBlock).block is BlockModLeaves) 100 else 0

        val DECAYABLE: PropertyBool = PropertyBool.create("decayable")
        val CHECK_DECAY: PropertyBool = PropertyBool.create("check_decay")

        val DECAY_BIT = 8
        val CHECK_BIT = 4

        init {
            MinecraftForge.EVENT_BUS.register(this)
            GameRegistry.registerFuelHandler(this)
        }

        var lastFancy = false
        var fancyLeaves = false

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        fun onRenderWorldLast(e: RenderWorldLastEvent) {
            lastFancy = fancyLeaves
            fancyLeaves = Minecraft.getMinecraft().gameSettings.fancyGraphics
            e.context
        }
    }


    open val canBeOpaque: Boolean
        get() = true
    internal var surroundings: IntArray = IntArray(32 * 32 * 32)

    init {
        this.tickRandomly = true
        this.setHardness(0.2f)
        this.setLightOpacity(1)
        this.soundType = SoundType.PLANT
        if (itemForm != null)
            for (variant in this.variants.indices)
                OreDictionary.registerOre("treeLeaves", ItemStack(this, 1, variant))
    }

    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 60
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 30

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i = 1
        val j = i + 1
        val k = pos.x
        val l = pos.y
        val i1 = pos.z

        if (worldIn.isAreaLoaded(BlockPos(k - j, l - j, i1 - j), BlockPos(k + j, l + j, i1 + j))) {
            for (j1 in -i..i) {
                for (k1 in -i..i) {
                    for (l1 in -i..i) {
                        val blockpos = pos.add(j1, k1, l1)
                        val iblockstate = worldIn.getBlockState(blockpos)

                        if (iblockstate.block.isLeaves(iblockstate, worldIn, blockpos)) {
                            iblockstate.block.beginLeavesDecay(iblockstate, worldIn, blockpos)
                        }
                    }
                }
            }
        }
    }

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(CHECK_DECAY, DECAYABLE)

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random?) {
        if (!worldIn.isRemote) {
            if (state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
                val i = 4
                val j = i + 1
                val k = pos.x
                val l = pos.y
                val i1 = pos.z
                val j1 = 32
                val k1 = j1 * j1
                val l1 = j1 / 2

                if (worldIn.isAreaLoaded(BlockPos(k - j, l - j, i1 - j), BlockPos(k + j, l + j, i1 + j))) {
                    val mutpos = BlockPos.MutableBlockPos()

                    for (i2 in -i..i) {
                        for (j2 in -i..i) {
                            for (k2 in -i..i) {
                                val iblockstate = worldIn.getBlockState(mutpos.setPos(k + i2, l + j2, i1 + k2))
                                val block = iblockstate.block

                                if (!block.canSustainLeaves(iblockstate, worldIn, mutpos.setPos(k + i2, l + j2, i1 + k2))) {
                                    if (block.isLeaves(iblockstate, worldIn, mutpos.setPos(k + i2, l + j2, i1 + k2))) {
                                        surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -2
                                    } else {
                                        surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = -1
                                    }
                                } else {
                                    surroundings[(i2 + l1) * k1 + (j2 + l1) * j1 + k2 + l1] = 0
                                }
                            }
                        }
                    }

                    for (i3 in 1..4) {
                        for (j3 in -i..i) {
                            for (k3 in -i..i) {
                                for (l3 in -i..i) {
                                    if (surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1] == i3 - 1) {
                                        if (surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            surroundings[(j3 + l1 - 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3
                                        }

                                        if (surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] == -2) {
                                            surroundings[(j3 + l1 + 1) * k1 + (k3 + l1) * j1 + l3 + l1] = i3
                                        }

                                        if (surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] == -2) {
                                            surroundings[(j3 + l1) * k1 + (k3 + l1 - 1) * j1 + l3 + l1] = i3
                                        }

                                        if (surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] == -2) {
                                            surroundings[(j3 + l1) * k1 + (k3 + l1 + 1) * j1 + l3 + l1] = i3
                                        }

                                        if (surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] == -2) {
                                            surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + (l3 + l1 - 1)] = i3
                                        }

                                        if (surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] == -2) {
                                            surroundings[(j3 + l1) * k1 + (k3 + l1) * j1 + l3 + l1 + 1] = i3
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val l2 = surroundings[l1 * k1 + l1 * j1 + l1]

                if (l2 >= 0) {
                    worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4)
                } else {
                    this.destroy(worldIn, pos)
                }
            }
        }
    }

    private fun destroy(worldIn: World, pos: BlockPos) {
        this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0)
        worldIn.setBlockToAir(pos)
    }

    @SideOnly(Side.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState?, worldIn: World?, pos: BlockPos?, rand: Random?) {
        if (worldIn!!.isRainingAt(pos!!.up()) && !worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && rand!!.nextInt(15) == 1) {
            val d0 = (pos.x.toFloat() + rand.nextFloat()).toDouble()
            val d1 = pos.y.toDouble() - 0.05
            val d2 = (pos.z.toFloat() + rand.nextFloat()).toDouble()
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0, 0.0, 0.0, *IntArray(0))
        }

        if (fancyLeaves != lastFancy) {
            worldIn.markBlockRangeForRenderUpdate(pos, pos)
        }
    }


    override fun quantityDropped(random: Random?): Int {
        return if (random!!.nextInt(20) == 0) 1 else 0
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        worldIn?.setBlockState(pos, state?.withProperty(DECAYABLE, false))
    }

    abstract override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item?

    override fun dropBlockAsItemWithChance(worldIn: World, pos: BlockPos, state: IBlockState, chance: Float, fortune: Int) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune)
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return !fancyLeaves && canBeOpaque
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer {
        return if (fancyLeaves || !canBeOpaque) BlockRenderLayer.CUTOUT_MIPPED else BlockRenderLayer.SOLID
    }

    override fun isShearable(item: ItemStack, world: IBlockAccess, pos: BlockPos): Boolean {
        return true
    }

    override fun isLeaves(state: IBlockState, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun beginLeavesDecay(state: IBlockState?, world: World?, pos: BlockPos?) {
        if (!(state!!.getValue(CHECK_DECAY) as Boolean)) {
            world!!.setBlockState(pos, state.withProperty(CHECK_DECAY, true), 4)
        }
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val check = (meta and CHECK_BIT) == 0
        val decayable = (meta and DECAY_BIT) == 0
        return defaultState.withProperty(CHECK_DECAY, check).withProperty(DECAYABLE, decayable)
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        state ?: return 0
        var meta = 0
        if (state.getValue(CHECK_DECAY))
            meta = meta or CHECK_BIT

        if (state.getValue(DECAYABLE))
            meta = meta or DECAY_BIT

        return meta
    }

    override fun getDrops(world: IBlockAccess?, pos: BlockPos?, state: IBlockState, fortune: Int): List<ItemStack> {
        val ret = ArrayList<ItemStack>()
        val rand = if (world is World) world.rand else Random()
        var chance = 20

        if (fortune > 0) {
            chance -= 2 shl fortune
            if (chance < 10) chance = 10
        }

        if (rand.nextInt(chance) == 0)
            ret.add(ItemStack(getItemDropped(state, rand, fortune), 1, damageDropped(state)))

        this.captureDrops(true)
        ret.addAll(this.captureDrops(false))
        return ret
    }

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, DECAYABLE, CHECK_DECAY)
    }

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return if (!fancyLeaves && canBeOpaque && blockAccess.getBlockState(pos.offset(side)).block === this) false else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

    override fun onSheared(item: ItemStack?, world: IBlockAccess, pos: BlockPos, fortune: Int): MutableList<ItemStack>? {
        return mutableListOf(ItemStack(this, 1, getMetaFromState(world.getBlockState(pos).withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false))))
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World?, pos: BlockPos?, player: EntityPlayer?): ItemStack {
        return ItemStack(this, 1, getMetaFromState(state.withProperty(DECAYABLE, false).withProperty(CHECK_DECAY, false)))
    }
}

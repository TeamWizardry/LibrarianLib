package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.generateBaseItemModel
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import net.minecraft.block.Block
import net.minecraft.block.BlockSlab
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*


/**
 * @author WireSegal
 * Created at 9:50 AM on 1/10/17.
 */
@Suppress("LeakingThis")
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
open class BlockModSlab(name: String, val parent: IBlockState) : BlockSlab(wrapMaterial(parent.material)), IModBlock, IModelGenerator {

    private val parentName = parent.block.key

    open val singleBlock: BlockModSlab = this

    protected inner class BlockDouble(name: String, parent: IBlockState) : BlockModSlab(name, parent) {
        override val singleBlock: BlockModSlab = this@BlockModSlab

        override fun isDouble() = true

        override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
            val name = ResourceLocation(parentName.namespace, "blocks/${parentName.path}").toString()
            val loc = singleBlock.key.path + "_full"
            ModelHandler.generateBlockJson(this, {
                generateBlockStates(this, mapper) {
                    "model"("${key.namespace}:$loc")
                }
            }, {
                getPathForBlockModel(this, loc) to {
                    "parent"("block/cube_all")
                    "textures" {
                        "all"(name)
                    }
                }
            })
            return true
        }

        override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
            return false
        }
    }

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun canBurn(e: FurnaceFuelBurnTimeEvent) {
            val fuel = e.itemStack
            if (e.burnTime == -1)
                if (fuel.item is ItemBlock && (fuel.item as ItemBlock).block.defaultState.material == FAKE_WOOD)
                    e.burnTime = 150
        }

        val DUMMY_PROP: PropertyEnum<Dummy> = PropertyEnum.create("block", Dummy::class.java)

        fun wrapMaterial(material: Material) = if (material == Material.WOOD) FAKE_WOOD else material

        val FAKE_WOOD = object : Material(MapColor.WOOD) {
            init {
                setBurning()
            }
        }

    }


    override val variants = VariantHelper.beginSetupBlock(name, arrayOf())
    val doubleBlock: BlockSlab = if (!isDouble) createDoubleForm(name) else this

    override val bareName: String = VariantHelper.toSnakeCase(name)
    val modId = currentModId

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
    }


    override fun setTranslationKey(name: String): Block {
        super.setTranslationKey(name)
        VariantHelper.setTranslationKeyForBlock(this, modId, name, itemForm)
        return this
    }

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? {
        return if (isDouble) null else ItemModSlab(this)
    }


    /**
     * Override this to have a custom BlockDouble implementation.
     */
    protected open fun createDoubleForm(name: String): BlockDouble {
        return BlockDouble(name + "_full", parent)
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]


    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor = parent.getMapColor(worldIn, pos)
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)

    @Suppress("OverridingDeprecatedMember")
    @SideOnly(Side.CLIENT)
    override fun isTranslucent(state: IBlockState) = parent.isTranslucent

    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent) || (material == FAKE_WOOD && type == "axe")
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent) ?: if (material == FAKE_WOOD) "axe" else null

    override fun createBlockState()
            = if (isDouble) BlockStateContainer(this, DUMMY_PROP)
    else BlockStateContainer(this, HALF, DUMMY_PROP)

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(DUMMY_PROP)

    @Suppress("OverridingDeprecatedMember")
    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, side: EnumFacing): BlockFaceShape {
        return if (isDouble) BlockFaceShape.SOLID
        else if (side == EnumFacing.UP && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) BlockFaceShape.SOLID
        else if (side == EnumFacing.DOWN && state.getValue(HALF) == BlockSlab.EnumBlockHalf.BOTTOM) BlockFaceShape.SOLID
        else BlockFaceShape.UNDEFINED
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return singleBlock.itemForm
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState
            = if (isDouble) defaultState
    else defaultState.withProperty(BlockSlab.HALF, if (meta == 8) EnumBlockHalf.TOP else EnumBlockHalf.BOTTOM)

    override fun getMetaFromState(state: IBlockState)
            = when {
                isDouble -> 0
                state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP -> 8
                else -> 0
            }

    // Internal fixes for slab overriding

    override fun getTypeForItem(stack: ItemStack) = Dummy.SLAB
    override fun isDouble() = false
    override fun getTranslationKey(meta: Int): String = translationKey
    override fun getVariantProperty() = DUMMY_PROP

    enum class Dummy : EnumStringSerializable {
        SLAB
    }

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.namespace, "blocks/${parentName.path}").toString()
        val simpleName = key.path

        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                if (it == "half=bottom") "model"("${registryName}_bottom")
                else if (it == "half=top") "model"("${registryName}_top")
            }
        }, {
                    getPathForBlockModel(this, "${simpleName}_bottom") to {
                        "parent"("block/half_slab")
                        "textures" {
                            "bottom"(name)
                            "top"(name)
                            "side"(name)
                        }
                    }

                    getPathForBlockModel(this, "${simpleName}_top") to {
                        "parent"("block/upper_slab")
                        "textures" {
                            "bottom"(name)
                            "top"(name)
                            "side"(name)
                        }

                    }
        })
        return true
    }

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        ModelHandler.generateItemJson(item) {
            getPathForItemModel(this) to
                    generateBaseItemModel(this, "${key.path}_bottom")
        }
        return true
    }
}



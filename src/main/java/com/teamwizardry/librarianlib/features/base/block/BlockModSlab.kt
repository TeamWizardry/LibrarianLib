package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils.getPathForBlockModel
import net.minecraft.block.Block
import net.minecraft.block.BlockSlab
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
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
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*


/**
 * @author WireSegal
 * Created at 9:50 AM on 1/10/17.
 */
@Suppress("LeakingThis")
open class BlockModSlab(name: String, val parent: IBlockState) : BlockSlab(wrapMaterial(parent.material)), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    open val singleBlock: BlockModSlab = this

    protected inner class BlockDouble(name: String, parent: IBlockState) : BlockModSlab(name, parent) {
        override val singleBlock: BlockModSlab = this@BlockModSlab

        override fun isDouble() = true

        override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
            ModelHandler.generateBlockJson(this, {
                JsonGenerationUtils.generateBlockStates(this, mapper) {
                    json { obj("model" to parentName.toString()) }
                }
            }, {
                mapOf()
            })
            return true
        }

        override fun generateMissingItem(variant: String): Boolean {
            return false
        }
    }

    companion object : IFuelHandler {
        override fun getBurnTime(fuel: ItemStack): Int {
            return if (fuel.item is ItemBlock &&
                    (fuel.item as ItemBlock).block.defaultState.material == FAKE_WOOD) 150 else 0
        }

        init {
            GameRegistry.registerFuelHandler(this)
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

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
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


    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @Suppress("OverridingDeprecatedMember")
    @SideOnly(Side.CLIENT) override fun isTranslucent(state: IBlockState?) = parent.isTranslucent
    override fun isToolEffective(type: String?, state: IBlockState) = parent.block.isToolEffective(type, parent) || (blockMaterial == FAKE_WOOD && type == "axe")
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent) ?: if (blockMaterial == FAKE_WOOD) "axe" else null

    override fun createBlockState()
            = if (isDouble) BlockStateContainer(this, DUMMY_PROP)
    else BlockStateContainer(this, HALF, DUMMY_PROP)

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(DUMMY_PROP)

    @Suppress("DEPRECATION")
    override fun isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        val state = getActualState(base_state, world, pos)
        return isDouble
                || state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP && side == EnumFacing.UP
                || state.getValue(BlockSlab.HALF) == EnumBlockHalf.BOTTOM && side == EnumFacing.DOWN
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return singleBlock.itemForm
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState
            = if (isDouble) defaultState
    else defaultState.withProperty(BlockSlab.HALF, if (meta == 8) EnumBlockHalf.TOP else EnumBlockHalf.BOTTOM)

    override fun getMetaFromState(state: IBlockState)
            = if (isDouble) 0
    else if (state.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP) 8 else 0

    // Internal fixes for slab overriding

    override fun getTypeForItem(stack: ItemStack?) = Dummy.SLAB
    override fun isDouble() = false
    override fun getUnlocalizedName(meta: Int): String = unlocalizedName
    override fun getVariantProperty() = DUMMY_PROP

    enum class Dummy : EnumStringSerializable {
        SLAB
    }

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName!!.resourcePath}").toString()
        val simpleName = registryName!!.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                when (it) {
                    "half=bottom" -> json { obj("model" to "${registryName}_bottom") }
                    "half=top" -> json { obj("model" to "${registryName}_top") }
                    else -> json { obj() }
                }
            }
        }, {
            mapOf(
                    getPathForBlockModel(this, "${simpleName}_bottom") to json {
                        obj(
                                "parent" to "block/half_slab",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    getPathForBlockModel(this, "${simpleName}_top") to json {
                        obj(
                                "parent" to "block/upper_slab",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    }
            )
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item as Item)
                    to JsonGenerationUtils.generateBaseItemModel(item, "${item.registryName!!.resourcePath}_bottom"))
        }
        return true
    }
}



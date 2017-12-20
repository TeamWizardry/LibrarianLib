package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockStairs
import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 10:36 AM on 1/10/17.
 */
@Suppress("LeakingThis")
open class BlockModStairs(name: String, val parent: IBlockState) : BlockStairs(parent), IModBlock, IModelGenerator {


    private val parentName = parent.block.registryName

    override val bareName: String = VariantHelper.toSnakeCase(name)
    val modId = currentModId
    override val variants: Array<out String> = VariantHelper.beginSetupBlock(name, arrayOf())

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor = parent.getMapColor(worldIn, pos)
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @Suppress("OverridingDeprecatedMember")
    @SideOnly(Side.CLIENT)
    override fun isTranslucent(state: IBlockState) = parent.isTranslucent
    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

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

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = registryName!!.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                val x = if ("half=top" in it) 180 else 0
                var y = if ("facing=east" in it) 0
                else if ("facing=west" in it) 180
                else if ("facing=south" in it) 90
                else 270
                if ("half=top" in it && ("shape=inner" in it || "shape=outer" in it)) y += 90
                if ("_left" in it) y += 270
                y %= 360

                val modelType = if ("shape=straight" in it) "" else if ("shape=inner" in it) "_inner" else "_outer"
                json {
                    obj(
                            "model" to "$registryName$modelType",
                            *if (x != 0) arrayOf("x" to x) else arrayOf(),
                            *if (y != 0) arrayOf("y" to y) else arrayOf(),
                            *if (x != 0 || y != 0) arrayOf("uvlock" to true) else arrayOf()
                    )
                }
            }
        }, {
            mapOf(
                    JsonGenerationUtils.getPathForBlockModel(this, simpleName) to json {
                        obj(
                                "parent" to "block/stairs",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_inner") to json {
                        obj(
                                "parent" to "block/inner_stairs",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_outer") to json {
                        obj(
                                "parent" to "block/outer_stairs",
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
                    to JsonGenerationUtils.generateBaseItemModel(item))
        }
        return true
    }
}

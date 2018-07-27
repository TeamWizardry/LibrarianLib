package com.teamwizardry.librarianlib.features.base.block

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
import net.minecraft.block.BlockStairs
import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
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

    private val parentName = parent.block.key

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

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = key.resourcePath

        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                val x = if ("half=top" in it) 180 else 0
                var y = when {
                    "facing=east" in it -> 0
                    "facing=west" in it -> 180
                    "facing=south" in it -> 90
                    else -> 270
                }
                if ("half=top" in it && ("shape=inner" in it || "shape=outer" in it)) y += 90
                if ("_left" in it) y += 270
                y %= 360

                val modelType = if ("shape=straight" in it) "" else if ("shape=inner" in it) "_inner" else "_outer"

                "model"("$registryName$modelType")

                if (x != 0)
                    "x"(x)
                if (y != 0)
                    "y"(y)
                if (x != 0 || y != 0)
                    "uvlock"(true)
            }
        }, {
            getPathForBlockModel(this, simpleName) to {
                "parent"("block/stairs")
                "textures" {
                    "bottom"(name)
                    "top"(name)
                    "side"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_inner") to {
                "parent"("block/inner_stairs")
                "textures" {
                    "bottom"(name)
                    "top"(name)
                    "side"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_outer") to {
                "parent"("block/outer_stairs")
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
            getPathForItemModel(this) to generateBaseItemModel(this)
        }
        return true
    }
}

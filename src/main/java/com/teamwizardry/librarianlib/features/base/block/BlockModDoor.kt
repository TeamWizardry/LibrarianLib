package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import net.minecraft.block.Block
import net.minecraft.block.BlockDoor
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
import java.util.*

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
open class BlockModDoor(name: String, val parent: IBlockState) : BlockDoor(parent.material), IModBlock, IModelGenerator {

    override val variants: Array<out String>

    override val bareName: String = VariantHelper.toSnakeCase(name)
    val modId = currentModId

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    val doorItemForm: Item?

    init {
        doorItemForm = createDoorItemForm()
        this.variants = VariantHelper.beginSetupBlock(name, arrayOf())
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
        return null
    }

    open fun createDoorItemForm(): Item? {
        return ItemModDoor(this, bareName)
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) null else this.doorItemForm
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor = parent.getMapColor(worldIn, pos)
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(key.resourceDomain, "blocks/${key.resourcePath}").toString()
        val simpleName = key.resourcePath

        ModelHandler.generateBlockJson(block, {
            generateBlockStates(this, mapper) {
                val suffix = (if ("half=lower" in it) "bottom" else "top") + if (("open=true" in it) xor ("hinge=left" in it)) "_rh" else ""
                var rot = if ("facing=east" in it) 0 else if ("facing=south" in it) 90 else if ("facing=west" in it) 180 else 270
                if ("hinge=left" in it && "open=true" in it) rot += 90
                else if ("hinge=right" in it && "open=true" in it) rot += 270
                rot %= 360

                "model"("${key}_$suffix")
                if (rot != 0)
                    "y"(rot)
            }
        }, {
            getPathForBlockModel(this, "${simpleName}_bottom") to {
                "parent"("block/door_bottom")
                "textures" {
                    "bottom"(name + "_door_lower")
                    "top"(name + "_door_upper")
                }
            }

            getPathForBlockModel(this, "${simpleName}_top") to {
                "parent"("block/door_top")
                "textures" {
                    "bottom"(name + "_door_lower")
                    "top"(name + "_door_upper")
                }
            }
            getPathForBlockModel(this, "${simpleName}_bottom_rh") to {
                "parent"("block/door_bottom_rh")
                "textures" {
                    "bottom"(name + "_door_lower")
                    "top"(name + "_door_upper")
                }
            }
            getPathForBlockModel(this, "${simpleName}_top_rh") to {
                "parent"("block/door_top_rh")
                "textures" {
                    "bottom"(name + "_door_lower")
                    "top"(name + "_door_upper")
                }
            }
        })
        return true
    }
}

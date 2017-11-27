package com.teamwizardry.librarianlib.features.base.fluid

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.IModBlock
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.associateInPlace
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fluids.BlockFluidBase
import net.minecraftforge.fluids.BlockFluidClassic
import net.minecraftforge.fluids.Fluid

/**
 * @author WireSegal
 * Created at 8:18 AM on 11/23/17.
 */
@Suppress("LeakingThis")
open class BlockModFluid(fluid: Fluid, material: Material) : BlockFluidClassic(fluid, material), IModBlock, IModelGenerator {

    override val bareName: String = VariantHelper.toSnakeCase(fluid.name)
    override val variants: Array<out String> = VariantHelper.beginSetupBlock(bareName, arrayOf())
    val modId = currentModId

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(BlockFluidBase.LEVEL)

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? = null

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    override fun generateMissingBlockstate(mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val intMapper = mapper ?: DefaultStateMapper()::putStateModelLocations
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.getPathsForBlockstate(this, mapper).associateInPlace {
                json {
                    obj("forge_marker" to 1,
                            "variants" to obj(
                                    *intMapper(this@BlockModFluid).map { (_, mrl) ->
                                        mrl.variant to obj(
                                                "model" to "forge:fluid",
                                                "custom" to obj("fluid" to fluidName))
                                    }.toTypedArray()))
                }
            }
        }, { mapOf() })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item as Item, variant) to json {
                obj(
                        "parent" to "item/generated",
                        "textures" to obj(
                                "layer0" to fluid.flowing.toString()
                        )
                )
            })
        }
        return true
    }
}

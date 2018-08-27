package com.teamwizardry.librarianlib.features.base.fluid

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.IModBlock
import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import com.teamwizardry.librarianlib.features.utilities.getPathsForBlockstate
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
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

    override fun setTranslationKey(name: String): Block {
        super.setTranslationKey(name)
        VariantHelper.setTranslationKeyForBlock(this, modId, name, itemForm)
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

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val intMapper = mapper ?: DefaultStateMapper()::putStateModelLocations
        ModelHandler.generateBlockJson(this, {
            for (path in getPathsForBlockstate(this, mapper))
                path to {
                    "forge_marker"(1)
                    "variants" {
                        for ((_, mrl) in intMapper(this@BlockModFluid))
                            mrl.variant to {
                                "model"("forge:fluid")
                                "custom" {
                                    "fluid"(fluidName)
                                }
                            }
                    }
                }
        }, { })
        return true
    }

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        ModelHandler.generateItemJson(item) {
            getPathForItemModel(this, variant) to {
                "parent"("item/generated")
                "textures" {
                    "layer0"(fluid.flowing)
                }
            }
        }
        return true
    }
}

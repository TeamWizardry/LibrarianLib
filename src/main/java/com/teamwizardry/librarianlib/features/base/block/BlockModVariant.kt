package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.utilities.generateBaseBlockModel
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * The default implementation for an IModBlock that defines its variants based on item variants.
 */
@Suppress("LeakingThis")
open class BlockModVariant(name: String, materialIn: Material, color: MapColor, vararg variants: String) : BlockMod(name, materialIn, color, *injectNames(name, variants)), IModBlock, IModelGenerator {

    constructor(name: String, materialIn: Material, vararg variants: String) : this(name, materialIn, materialIn.materialMapColor, *variants)

    companion object {
        private var lastNames: Array<out String> by threadLocal {
            arrayOf<String>()
        }

        /**
         * Hacky nonsense required because constructor and associated arguments
         * aren't available until super's construction is complete.
         *
         * This captures the variants during construction and injects them into the [property]
         * created by first access in [createBlockState].
         */
        private fun injectNames(name: String, variants: Array<out String>): Array<out String> {
            lastNames = VariantHelper.beginSetupBlock(name, variants)
            return variants
        }
    }

    lateinit var property: PropertyString
        private set

    override fun createBlockState(): BlockStateContainer {
        property = PropertyString("variant", *lastNames)
        return BlockStateContainer(this, property)
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)
    override fun getMetaFromState(state: IBlockState) = property.getMetaFromName(state.getValue(property))
    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(property, property.getNameFromMeta(meta))

    // Blockstate related objects

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                "model" to key.namespace + ":" + it.replace("variant=", "")
            }
        }, {
            for (variant in variants)
                getPathForBlockModel(this, variant) to generateBaseBlockModel(this, variant)
        })
        return true
    }
}

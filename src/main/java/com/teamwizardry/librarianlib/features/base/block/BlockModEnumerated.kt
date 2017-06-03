package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.IStringSerializable

/**
 * The default implementation for an IModBlock that defines its variants based on an enumeration.
 */
@Suppress("LeakingThis", "UNCHECKED_CAST")
open class BlockModEnumerated<T> @JvmOverloads constructor(name: String, materialIn: Material, color: MapColor, clazz: Class<T>, predicate: ((T) -> Boolean)? = null)
    : BlockMod(name, materialIn, color, *injectNames(name, clazz, predicate as (Enum<*>) -> Boolean)), IModBlock, IModelGenerator where T : Enum<T>, T : IStringSerializable {

    @JvmOverloads constructor(name: String, materialIn: Material, clazz: Class<T>, predicate: ((T) -> Boolean)? = null) : this(name, materialIn, materialIn.materialMapColor, clazz, predicate)

    open operator fun get(value: T): IBlockState = defaultState.withProperty(property, value)

    companion object {
        private var lastClass: Class<out Enum<*>>? by threadLocal()
        private var lastPredicate: ((Enum<*>) -> Boolean)? by threadLocal()

        /**
         * Hacky nonsense required because constructor and associated arguments
         * aren't available until super's construction is complete.
         *
         * This captures the variants during construction and injects them into the [property]
         * created by first access in [createBlockState].
         */
        private fun injectNames(name: String, clazz: Class<out Enum<*>>, predicate: (Enum<*>) -> Boolean): Array<out String> {
            val variants = VariantHelper.beginSetupBlock(name, clazz.enumConstants.map { name + "_" + VariantHelper.toSnakeCase(it.name) }.toTypedArray())
            lastClass = clazz
            lastPredicate = predicate
            return variants
        }
    }

    lateinit var property: PropertyEnum<T>
        private set

    private val values: Array<out T> by lazy {
        property.valueClass.enumConstants
    }

    override fun createBlockState(): BlockStateContainer {

        val predicate = lastPredicate
        property = if (predicate == null)
            PropertyEnum.create("variant", lastClass as Class<T>)
        else
            PropertyEnum.create("variant", lastClass as Class<T>) {
                if (it != null) predicate(it) else false
            }
        return BlockStateContainer(this, property)
    }

    override fun damageDropped(state: IBlockState) = getMetaFromState(state)
    override fun getMetaFromState(state: IBlockState) = state.getValue(property).ordinal
    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(property, values[meta % values.size])

    // Blockstate related objects

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                json { obj("model" to registryName!!.resourceDomain + ":" + it.replace("variant=", "")) }
            }
        }, {
            variants.associate { JsonGenerationUtils.getPathForBlockModel(this, it) to JsonGenerationUtils.generateBaseBlockModel(this, it) }
        })
        return true
    }
}

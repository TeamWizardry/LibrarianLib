package com.teamwizardry.librarianlib.features.base.item

import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import java.util.function.Predicate

/**
 * @author WireSegal
 * Created at 9:43 PM on 6/17/17.
 */
class ItemModEnumerated<T>(name: String, enumClass: Class<T>, predicate: ((T) -> Boolean)?)
    : ItemMod(name, *enumToVariants(name, enumClass, predicate))
        where T : IStringSerializable, T: Enum<T> {

    constructor(name: String, enumClass: Class<T>) : this(name, enumClass, null)

    companion object {
        private fun <T> enumToVariants(name: String, enumClass: Class<T>, predicate: ((T) -> Boolean)?)
                where T : IStringSerializable, T: Enum<T> =
                    enumClass.enumConstants.filter(predicate ?: { true }).map { name + "_" + it.getName() }.toTypedArray()
    }

    private val values = enumClass.enumConstants.filter(predicate ?: { true })

    fun variantOfStack(stack: ItemStack): T = values[stack.itemDamage % values.size]
    fun stackOfVariant(variant: T) = ItemStack(this, 1, values.indexOf(variant))
}

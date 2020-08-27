package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.item.ItemGroup
import java.util.function.Function

/**
 * A wrapper around an item's [ItemGroup], allowing deferred evaluation based on the [RegistrationManager].
 */
class ItemGroupSpec(private val getter: Function<RegistrationManager, ItemGroup?>) {

    fun get(manager: RegistrationManager): ItemGroup? {
        return getter.apply(manager)
    }

    companion object {
        @JvmField
        val NONE: ItemGroupSpec = ItemGroupSpec( Function { null })
        @JvmField
        val DEFAULT: ItemGroupSpec = ItemGroupSpec( Function { it.itemGroup })
    }
}
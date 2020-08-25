package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.item.ItemGroup

/**
 * A wrapper around an item's [ItemGroup], allowing deferred evaluation based on the [RegistrationManager].
 */
class ItemGroupSpec(private val getter: (RegistrationManager) -> ItemGroup?) {

    fun get(manager: RegistrationManager): ItemGroup? {
        return getter(manager)
    }

    companion object {
        @JvmStatic
        val NONE: ItemGroupSpec = ItemGroupSpec { null }
        @JvmStatic
        val DEFAULT: ItemGroupSpec = ItemGroupSpec { it.itemGroup }
    }
}
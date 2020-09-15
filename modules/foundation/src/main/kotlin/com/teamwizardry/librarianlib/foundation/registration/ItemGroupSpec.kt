package com.teamwizardry.librarianlib.foundation.registration

import net.minecraft.item.ItemGroup
import java.util.function.Function

/**
 * A wrapper around an item's [ItemGroup], allowing deferred evaluation based on the [RegistrationManager].
 */
public class ItemGroupSpec(private val getter: Function<RegistrationManager, ItemGroup?>) {

    public fun get(manager: RegistrationManager): ItemGroup? {
        return getter.apply(manager)
    }

    public companion object {
        @JvmField
        public val NONE: ItemGroupSpec = ItemGroupSpec { null }

        @JvmField
        public val DEFAULT: ItemGroupSpec = ItemGroupSpec { it.itemGroup }
    }
}
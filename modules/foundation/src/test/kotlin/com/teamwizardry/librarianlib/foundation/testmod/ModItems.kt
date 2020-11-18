package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.ItemSpec
import com.teamwizardry.librarianlib.foundation.registration.LazyItem
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestCapabilityItem
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestItem
import net.minecraft.item.Item

object ModItems {
    val testItem: LazyItem = LazyItem()

    internal fun registerItems(registrationManager: RegistrationManager) {
        testItem.from(registrationManager.add(
            ItemSpec("test_item")
                .maxStackSize(16)
                .item { TestItem(it.itemProperties) }
                .datagen { simpleModel() }
        ))
        testItem.from(registrationManager.add(
            ItemSpec("test_capabilities")
                .maxStackSize(1)
                .item { TestCapabilityItem(it.itemProperties) }
                .datagen { simpleModel() }
        ))
    }
}
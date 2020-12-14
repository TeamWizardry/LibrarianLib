package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.ItemSpec
import com.teamwizardry.librarianlib.foundation.registration.LazyItem
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestCapabilityItem
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.DirtSetterItem

object ModItems {
    val testItem: LazyItem = LazyItem()
    val testCapabilityItem: LazyItem = LazyItem()
    val dirtSetter: LazyItem = LazyItem()

    internal fun registerItems(registrationManager: RegistrationManager) {
        testItem.from(registrationManager.add(
            ItemSpec("test_item")
                .maxStackSize(16)
        ))
        testCapabilityItem.from(registrationManager.add(
            ItemSpec("test_capabilities")
                .maxStackSize(1)
                .item { TestCapabilityItem(it.itemProperties) }
        ))
        dirtSetter.from(registrationManager.add(
            ItemSpec("dirt_setter")
                .maxStackSize(1)
                .item { DirtSetterItem(it.itemProperties) }
                .datagen {
                    name("en_us", "Dirt Setter")
                }
        ))
    }
}
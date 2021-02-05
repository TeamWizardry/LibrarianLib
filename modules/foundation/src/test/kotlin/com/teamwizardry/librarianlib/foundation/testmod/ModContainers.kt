package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.ContainerScreenFactory
import com.teamwizardry.librarianlib.foundation.registration.LazyContainerType
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.DirtSetterContainer
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.DirtSetterContainerScreen
import com.teamwizardry.librarianlib.foundation.registration.ContainerSpec

object ModContainers {
    val dirtSetter: LazyContainerType<DirtSetterContainer> = LazyContainerType()

    internal fun registerContainers(registrationManager: RegistrationManager) {
        dirtSetter.from(registrationManager.add(
            ContainerSpec("dirt_setter", DirtSetterContainer::class.java) { ContainerScreenFactory(::DirtSetterContainerScreen) }
        ))
    }
}
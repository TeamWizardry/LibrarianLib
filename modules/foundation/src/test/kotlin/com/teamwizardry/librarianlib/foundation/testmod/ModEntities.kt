package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.EntitySpec
import com.teamwizardry.librarianlib.foundation.registration.LazyEntityType
import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.registration.TileEntitySpec
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestEntity
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileEntity
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.client.TestEntityRenderer
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.client.TestTileEntityRenderer
import net.minecraft.entity.EntityClassification
import java.util.function.Supplier

object ModEntities {
    val testEntity: LazyEntityType<TestEntity> = LazyEntityType()

    internal fun registerEntities(registrationManager: RegistrationManager) {
        testEntity.from(registrationManager.add(
            EntitySpec<TestEntity>("test_entity", EntityClassification.MISC) { type, world ->
                TestEntity(type, world)
            }
                .renderFactory(::TestEntityRenderer)
        ))
    }
}
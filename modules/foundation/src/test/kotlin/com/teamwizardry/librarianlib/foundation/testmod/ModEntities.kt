package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.*
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestEntity
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.client.TestEntityRenderer
import net.minecraft.entity.EntityClassification

object ModEntities {
    val testEntity: LazyEntityType<TestEntity> = LazyEntityType()

    internal fun registerEntities(registrationManager: RegistrationManager) {

        testEntity.from(registrationManager.add(
            EntitySpec<TestEntity>("test_entity", EntityClassification.MISC) { type, world ->
                TestEntity(type, world)
            }
                .renderFactory { EntityRendererFactory(::TestEntityRenderer) }
        ))
    }
}
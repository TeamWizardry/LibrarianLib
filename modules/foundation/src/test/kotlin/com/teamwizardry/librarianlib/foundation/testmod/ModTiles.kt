package com.teamwizardry.librarianlib.foundation.testmod

import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import com.teamwizardry.librarianlib.foundation.registration.RegistrationManager
import com.teamwizardry.librarianlib.foundation.registration.TileEntitySpec
import com.teamwizardry.librarianlib.foundation.testmod.customtypes.TestTileEntity
import java.util.function.Supplier

object ModTiles {
    val testTile: LazyTileEntityType = LazyTileEntityType()

    internal fun registerTileEntities(registrationManager: RegistrationManager) {
        testTile.from(registrationManager.add(
            TileEntitySpec("test_tile", Supplier { TestTileEntity() })
        ))
    }
}
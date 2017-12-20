package com.teamwizardry.librarianlib.test.worlddata

import com.teamwizardry.librarianlib.features.saving.Save
import com.teamwizardry.librarianlib.features.worlddata.WorldData
import com.teamwizardry.librarianlib.features.worlddata.WorldDataContainer

/**
 * TODO: Document file TestChunkData
 *
 * Created by TheCodeWarrior
 */
class TestWorldData(container: WorldDataContainer) : WorldData(container) {
    @Save var jumps: Int = 0
}

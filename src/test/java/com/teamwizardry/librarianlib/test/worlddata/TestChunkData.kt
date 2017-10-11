package com.teamwizardry.librarianlib.test.chunkdata

import com.teamwizardry.librarianlib.features.chunkdata.WorldData
import com.teamwizardry.librarianlib.features.chunkdata.WorldDataContainer
import com.teamwizardry.librarianlib.features.saving.Save

/**
 * TODO: Document file TestChunkData
 *
 * Created by TheCodeWarrior
 */
class TestWorldData(container: WorldDataContainer) : WorldData(container) {
    @Save var jumps: Int = 0
}

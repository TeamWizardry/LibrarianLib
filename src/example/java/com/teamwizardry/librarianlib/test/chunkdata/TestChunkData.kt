package com.teamwizardry.librarianlib.test.chunkdata

import com.teamwizardry.librarianlib.features.chunkdata.ChunkData
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.world.chunk.Chunk

/**
 * TODO: Document file TestChunkData
 *
 * Created by TheCodeWarrior
 */
class TestChunkData(chunk: Chunk) : ChunkData(chunk) {
    @Save var clicks: Int = 0
}

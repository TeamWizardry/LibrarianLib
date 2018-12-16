@file:JvmName("WorldUtils")
package com.teamwizardry.librarianlib.features.utilities

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.init.Blocks
import net.minecraft.util.BitArray
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.BlockStateContainer
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.storage.ExtendedBlockStorage

private var useFastIsAir = true
private val fastPos = BlockPos.MutableBlockPos()

@JvmName("fastIsAir")
fun World.fastIsAir(x: Int, y: Int, z: Int): Boolean {
    if(useFastIsAir) {
        try {
            if(fastCheckIsAir(this, x, y, z))
                return true // it's definitely air
        } catch(e: ReflectiveOperationException) {
            useFastIsAir = false // if cubic chunks or any other voodoo witchcraft, don't use fast isAir
        }
    }
    fastPos.setPos(x, y, z)
    val blockState = this.getBlockState(fastPos)
    return blockState.block == Blocks.AIR
}

private fun fastCheckIsAir(world: World, x: Int, y: Int, z: Int): Boolean {
    /* 1 */ val chunk = world.chunkProvider.getLoadedChunk(x shr 4, z shr 4) ?: return true

    /* 2 */ val storageArrays = chunk.storageArrays_mh
    /* 2 */ if(y < 0 || y shr 4 >= storageArrays.size) return true
    /* 2 */ val storage = storageArrays[y shr 4]
    /* 2 */ if(storage == Chunk.NULL_BLOCK_STORAGE) return true

    /* 3 */ val data = storage.data_mh
    /* 4 */ val index = (y and 0xf shl 8) or (z and 0xf shl 4) or (x and 0xf)
    /* 5 */ return data.storage_mh.getAt(index) == 0
}

/*

World.java:998

    public IBlockState getBlockState(BlockPos pos) {
        ...
1:      Chunk chunk = this.getChunk(pos);
        return chunk.getBlockState(pos);
    }

Chunk.java:518

    public IBlockState getBlockState(final int x, final int y, final int z) {
        ...
2:      if (y >= 0 && y >> 4 < this.storageArrays.length) {
2:          ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

2:          if (extendedblockstorage != NULL_BLOCK_STORAGE)
            {
4:              return extendedblockstorage.get(x & 15, y & 15, z & 15);
            }
        }
2:      return Blocks.AIR.getDefaultState();
        ...
    }

ExtendedBlockStorage.java:43

    public IBlockState get(int x, int y, int z) {
3:      return this.data.get(x, y, z);
    }

BlockStateContainer.java:93

    public IBlockState get(int x, int y, int z) {
        return this.get(
4:          getIndex(x, y, z)
        );
    }

ExtendedBlockStorage.java:26

    private static int getIndex(int x, int y, int z) {
4:      return y << 8 | z << 4 | x;
    }

BlockStateContainer.java:98

    protected IBlockState get(int index) {
        IBlockState iblockstate = this.palette.getBlockState(
5:          this.storage.getAt(index)
        );
        return iblockstate == null ? AIR_BLOCK_STATE : iblockstate;
    }

BlockStateContainer.java:58
    private void setBits(int bitsIn, boolean forceBits) {
        ...
5:      this.palette.idFor(AIR_BLOCK_STATE); <-- always initializes storage with air first, giving it id 0.
        ...
    }

 */

private val Chunk.storageArrays_mh by MethodHandleHelper.delegateForReadOnly<Chunk, Array<ExtendedBlockStorage>>(Chunk::class.java, "field_76652_q", "storageArrays")
private val ExtendedBlockStorage.data_mh by MethodHandleHelper.delegateForReadOnly<ExtendedBlockStorage, BlockStateContainer>(ExtendedBlockStorage::class.java, "field_177488_d", "data")
private val BlockStateContainer.storage_mh by MethodHandleHelper.delegateForReadOnly<BlockStateContainer, BitArray>(BlockStateContainer::class.java, "field_186021_b", "storage")

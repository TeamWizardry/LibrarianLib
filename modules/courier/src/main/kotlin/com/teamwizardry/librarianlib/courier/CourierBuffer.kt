package com.teamwizardry.librarianlib.courier

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.DecoderException
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.ChunkSectionPos

public class CourierBuffer(parent: ByteBuf) : PacketByteBuf(parent) {
    //@Environment(EnvType.CLIENT)
    override fun readLongArray(ls: LongArray?): LongArray {
        return this.readLongArray(ls, readableBytes() / 8)
    }

    //@Environment(EnvType.CLIENT)
    override fun readLongArray(toArray: LongArray?, maxCount: Int): LongArray {
        val count = readVarInt()
        val outArray = if(toArray?.size == count) {
            toArray
        } else {
            if (count > maxCount) {
                throw DecoderException("LongArray with size $count is bigger than allowed $maxCount")
            }
            LongArray(count)
        }

        for (k in outArray.indices) {
            outArray[k] = readLong()
        }

        return outArray
    }

    //@Environment(EnvType.CLIENT)
    override fun readChunkSectionPos(): ChunkSectionPos {
        return ChunkSectionPos.from(readLong())
    }

    //@Environment(EnvType.CLIENT)
    override fun readString(): String {
        return this.readString(32767)
    }

    public companion object {
        @JvmStatic
        public fun create(): CourierBuffer {
            return CourierBuffer(Unpooled.buffer())
        }
    }
}
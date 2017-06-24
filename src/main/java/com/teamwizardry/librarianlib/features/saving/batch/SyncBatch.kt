package com.teamwizardry.librarianlib.features.saving.batch

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * @author WireSegal
 * Created at 7:24 PM on 6/24/17.
 */
class SyncBatch(objects: List<(ByteBuf) -> Unit>) {
    val headerNum: Int

    val capacity = 30000

    val bytes: ByteArray

    init {
        val mainBuf = Unpooled.buffer()
        for (i in objects) {
            val tempBuf = Unpooled.buffer()
            i(tempBuf)
            mainBuf.writeInt(tempBuf.readableBytes())
            mainBuf.writeBytes(tempBuf)
        }
        headerNum = mainBuf.readableBytes() / capacity
        bytes = mainBuf.array()
    }

    /*
        Byte format for Syncing Batches

        <short: number of packets>
        <int: number of bytes total>
        <short: index of packet>
        <raw: raw data from the sync>
     */

    fun createByteDumps() = List(headerNum) {
        Unpooled.buffer()
                .writeShort(headerNum)
                .writeInt(bytes.size)
                .writeShort(it)
                .writeBytes(bytes.sliceArray(it * capacity..Math.min(bytes.size, (it + 1) * capacity)))
    }
}

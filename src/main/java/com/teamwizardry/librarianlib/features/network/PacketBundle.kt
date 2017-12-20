package com.teamwizardry.librarianlib.features.network

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister
import com.teamwizardry.librarianlib.features.saving.Save
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

/**
 * TODO: Document file PacketBundle
 *
 * Created by TheCodeWarrior
 */
@PacketRegister(Side.CLIENT)
class PacketBundle(@Save var discriminator: Int = -1, @Save var bufs: Array<ByteBuf> = emptyArray()) : PacketBase() {

    private var instances: List<PacketBase>? = null

    override fun readCustomBytes(buf: ByteBuf) {
        super.readCustomBytes(buf)
        val clazz = PacketHandler.getPacketClass(discriminator) ?: throw RuntimeException("Bundle packet discriminator $discriminator invalid!")
        instances = bufs.map { buffer ->
            val instance = clazz.newInstance() as PacketBase
            instance.fromBytes(buffer)
            return@map instance
        }
    }

    override fun handle(ctx: MessageContext) {
        instances?.forEach {
            it.handle(ctx)
        }
    }

    companion object {
        fun compactPackets(packets: Collection<PacketBase>, maxPacketSize: Int): List<PacketBundle> {
            if (packets.isEmpty()) return emptyList()

            val clazz = packets.first().javaClass
            if (packets.any { it.javaClass != clazz }) {
                throw IllegalArgumentException("PacketBundle can only transmit one packet type at a time!")
            }
            val discriminator = PacketHandler.getDiscriminator(clazz)
            val dataSize = maxPacketSize - 10 // - 10 to give space for the header and a little bit of leeway

            // begin writing packets to bytebufs and packing them in bins
            val bins = mutableListOf<Bin>()
            packets.forEach {
                val bytes = Unpooled.buffer()
                it.toBytes(bytes)

                val len = bytes.writerIndex()

                fun tryAdd(): Boolean {
                    return bins.any { bin ->
                        if (bin.canFit(len)) {
                            bin.add(bytes)
                            return@any true
                        }
                        return@any false
                    }
                }
                // pack in a bin if it can, if not add another bin, if it still can't complain that the packet is too
                // large to fit in any bin, even the empty one we just created.
                if (!tryAdd()) {
                    bins.add(Bin(dataSize))
                    if (!tryAdd()) {
                        throw IllegalArgumentException("Packet too large to fit in a bin! Packet is $len bytes long, but bins or only $dataSize in length.")
                    }
                }
            }

            return bins.map {
                PacketBundle(discriminator, it.contents.toTypedArray())
            }
        }

        private class Bin(val capacity: Int) {
            private var fullness: Int = 0
            val contents = mutableListOf<ByteBuf>()

            fun canFit(size: Int): Boolean {
                return capacity - fullness >= size
            }

            fun add(buf: ByteBuf) {
                contents.add(buf)
                fullness += buf.writerIndex()
            }
        }
    }
}

package com.teamwizardry.librarianlib.courier.test

import com.teamwizardry.librarianlib.courier.CourierBuffer
import com.teamwizardry.librarianlib.courier.CourierPacket
import com.teamwizardry.librarianlib.courier.CourierPacketType
import dev.thecodewarrior.prism.annotation.Refract
import dev.thecodewarrior.prism.annotation.RefractClass
import dev.thecodewarrior.prism.annotation.RefractConstructor
import net.minecraft.block.Block
import net.minecraft.util.Identifier

object TestPacketTypes {
    val testPacket = CourierPacketType(Identifier("liblib-courier-test:test_packet"), TestPacket::class.java)
}

@RefractClass
data class TestPacket @RefractConstructor constructor(
    @Refract("block") val block: Block,
    @Refract("value") val value: Int
) : CourierPacket {

    var manual: Int = 0

    override fun writeBytes(buffer: CourierBuffer) {
        buffer.writeVarInt(manual)
    }

    override fun readBytes(buffer: CourierBuffer) {
        manual = buffer.readVarInt()
    }
}


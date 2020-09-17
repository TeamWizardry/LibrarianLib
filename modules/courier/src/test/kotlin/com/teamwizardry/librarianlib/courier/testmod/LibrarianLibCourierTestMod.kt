package com.teamwizardry.librarianlib.courier.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import com.teamwizardry.librarianlib.courier.CourierChannel
import com.teamwizardry.librarianlib.courier.CourierPacket
import com.teamwizardry.librarianlib.courier.LibrarianLibCourierModule
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import dev.thecodewarrior.prism.annotation.Refract
import dev.thecodewarrior.prism.annotation.RefractClass
import dev.thecodewarrior.prism.annotation.RefractConstructor
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-courier-test")
object LibrarianLibCourierTestMod: TestMod(LibrarianLibCourierModule) {
    val channel: CourierChannel = CourierChannel(loc("librarianlib-courier-test", "courier"), "0")

    init {
        channel.registerCourierPacket<TestPacket>(NetworkDirection.PLAY_TO_CLIENT) { packet, context ->
            Client.player?.sendMessage(StringTextComponent("Register handler: $packet"))
        }

        +TestItem(TestItemConfig("server_to_client", "Server to client packet") {
            rightClick.server {
                val packet = TestPacket(Blocks.DIRT, 42)
                packet.manual = 100
                player.sendMessage(StringTextComponent("Server sending: $packet"))
                channel.send(PacketDistributor.PLAYER.with { player as ServerPlayerEntity }, packet)
            }
        })
    }
}

@RefractClass
data class TestPacket @RefractConstructor constructor(@Refract val block: Block, @Refract val value: Int): CourierPacket {
    var manual: Int = 0

    override fun writeBytes(buffer: PacketBuffer) {
        buffer.writeVarInt(manual)
    }

    override fun readBytes(buffer: PacketBuffer) {
        manual = buffer.readVarInt()
    }

    override fun handle(context: NetworkEvent.Context) {
        clientOnly {
            Client.player?.sendMessage(StringTextComponent("CourierPacket handler: $this"))
        }
    }
}


internal val logger = LibrarianLibCourierTestMod.makeLogger(null)

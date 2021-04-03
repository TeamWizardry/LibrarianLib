package com.teamwizardry.librarianlib.courier.testmod

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.core.util.sided.clientOnly
import com.teamwizardry.librarianlib.courier.CourierBuffer
import com.teamwizardry.librarianlib.courier.CourierChannel
import com.teamwizardry.librarianlib.courier.CourierPacket
import com.teamwizardry.librarianlib.courier.LibrarianLibCourierModule
import com.teamwizardry.librarianlib.testcore.TestMod
import com.teamwizardry.librarianlib.testcore.objects.TestItem
import dev.thecodewarrior.prism.annotation.Refract
import dev.thecodewarrior.prism.annotation.RefractClass
import dev.thecodewarrior.prism.annotation.RefractConstructor
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor

@Mod("ll-courier-test")
object LibrarianLibCourierTestMod: TestMod(LibrarianLibCourierModule) {
    val channel: CourierChannel = CourierChannel(loc("ll-courier-test", "courier"), "0")

    init {
        channel.registerCourierPacket<TestPacket>(NetworkDirection.PLAY_TO_CLIENT) { packet, context ->
            Client.player?.sendStatusMessage(StringTextComponent("Register handler: $packet"), false)
        }

        +TestItem(TestItemConfig("server_to_client", "Server to client packet") {
            rightClick.server {
                val packet = TestPacket(Blocks.DIRT, 42)
                packet.manual = 100
                player.sendStatusMessage(StringTextComponent("Server sending: $packet"), false)
                channel.send(PacketDistributor.PLAYER.with { player as ServerPlayerEntity }, packet)
            }
        })
    }
}

@RefractClass
data class TestPacket @RefractConstructor constructor(@Refract val block: Block, @Refract val value: Int): CourierPacket {
    var manual: Int = 0

    override fun writeBytes(buffer: CourierBuffer) {
        buffer.writeVarInt(manual)
    }

    override fun readBytes(buffer: CourierBuffer) {
        manual = buffer.readVarInt()
    }

    override fun handle(context: NetworkEvent.Context) {
        clientOnly {
            Client.player?.sendStatusMessage(StringTextComponent("CourierPacket handler: $this"), false)
        }
    }
}


internal val logger = LibrarianLibCourierTestMod.makeLogger(null)

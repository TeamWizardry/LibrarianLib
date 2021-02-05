package com.teamwizardry.librarianlib.courier.example;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.courier.PacketType;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ExamplePacketType extends PacketType<ExamplePacketType.Packet> {
    public ExamplePacketType() {
        super(Packet.class);
    }

    // the actual packet
    public static class Packet {
        public final BlockPos pos;
        public final Block block;

        public Packet(BlockPos pos, Block block) {
            this.pos = pos;
            this.block = block;
        }
    }

    @Override
    public void encode(Packet packet, @NotNull PacketBuffer buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeRegistryId(packet.block);
    }

    @Override
    public Packet decode(@NotNull PacketBuffer buffer) {
        BlockPos position = buffer.readBlockPos();
        Block block = buffer.readRegistryIdSafe(Block.class);
        return new Packet(position, block);
    }

    @Override
    public void handle(Packet packet, @NotNull Supplier<NetworkEvent.Context> context) {
        // check what side we're running on. Important when getting the player later
        if(context.get().getDirection().getReceptionSide().isServer()) {
            // run this on the main thread
            context.get().enqueueWork(() -> {
                // on the client you would do `player = Client.getPlayer()`
                PlayerEntity player = context.get().getSender();

                // **NEVER** trust the client. If we don't do this
                // it would allow a hacked client to generate and load
                // arbitrary chunks.
                if (!player.world.isBlockLoaded(packet.pos)) {
                    return;
                }
                if (player.world.getBlockState(packet.pos).getBlock() != packet.block) {
                    // do something
                }
            });
        }
    }
}

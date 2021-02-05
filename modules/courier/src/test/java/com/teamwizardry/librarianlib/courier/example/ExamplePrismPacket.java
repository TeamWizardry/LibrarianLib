package com.teamwizardry.librarianlib.courier.example;

import com.teamwizardry.librarianlib.core.util.Client;
import com.teamwizardry.librarianlib.courier.CourierPacket;
import dev.thecodewarrior.prism.annotation.Refract;
import dev.thecodewarrior.prism.annotation.RefractClass;
import dev.thecodewarrior.prism.annotation.RefractConstructor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

@RefractClass
public class ExamplePrismPacket implements CourierPacket {
    @Refract
    public final BlockPos pos;
    @Refract
    public final Block block;

    // parameter types and names match fields
    @RefractConstructor
    public ExamplePrismPacket(BlockPos pos, Block block) {
        this.pos = pos;
        this.block = block;
    }

    // optionally write anything not supported by Prism.
    @Override
    public void writeBytes(@NotNull PacketBuffer buffer) {
    }

    // optionally read anything not supported by Prism.
    // you'll need to use a non-final field and initialize it in this method.
    @Override
    public void readBytes(@NotNull PacketBuffer buffer) {
    }

    @Override
    public void handle(@NotNull NetworkEvent.Context context) {
        // check what side we're running on. Important when getting the player later
        if(context.getDirection().getReceptionSide().isServer()) {
            // run this on the main thread
            context.enqueueWork(() -> {
                // on the client you would do `player = Client.getPlayer()`
                PlayerEntity player = context.getSender();

                // **NEVER** trust the client. If we don't do this
                // it would allow a hacked client to generate and load
                // arbitrary chunks.
                if (!player.world.isBlockLoaded(this.pos)) {
                    return;
                }
                if (player.world.getBlockState(this.pos).getBlock() != this.block) {
                    // do something
                }
            });
        }
    }
}

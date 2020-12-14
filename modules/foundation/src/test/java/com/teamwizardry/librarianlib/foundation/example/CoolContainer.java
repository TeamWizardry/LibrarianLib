package com.teamwizardry.librarianlib.foundation.example;

import com.teamwizardry.librarianlib.facade.container.FacadeContainer;
import com.teamwizardry.librarianlib.facade.container.messaging.Message;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class CoolContainer extends FacadeContainer {
    private final BlockPos pos;

    public CoolContainer(
            int windowId, @NotNull PlayerEntity player,
            BlockPos pos
    ) {
        super(ExampleModContainers.coolContainer.get(), windowId, player);
        this.pos = pos;
    }

    @Message
    private void setToDirt(int offset) {
        // NEVER trust the client
        if(offset > 1) offset = 1;
        if(offset < -1) offset = -1;
        getPlayer().world.setBlockState(
                pos.add(0, offset, 0),
                Blocks.DIRT.getDefaultState()
        );
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}

package com.teamwizardry.librarianlib.facade.example.containers;

import com.teamwizardry.librarianlib.facade.container.FacadeController;
import com.teamwizardry.librarianlib.facade.container.messaging.Message;
import com.teamwizardry.librarianlib.facade.example.ExampleModContainers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class DirtSetterController extends FacadeController {
    private final BlockPos pos;

    public DirtSetterController(
            int windowId, @NotNull PlayerEntity player,
            BlockPos pos
    ) {
        super(ExampleModContainers.dirtSetterContainerType, windowId, player);
        this.pos = pos;
    }

    @Message
    private void setBlockPressed(int offset) {
        if(isClientContainer())
            return; // don't actually set the block on the client

        // NEVER trust the client
        if(offset > 1) offset = 1;
        if(offset < -1) offset = -1;
        getPlayer().world.setBlockState(
                pos.add(0, offset, 0),
                Blocks.DIRT.getDefaultState()
        );
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }
}

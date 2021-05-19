package com.teamwizardry.librarianlib.facade.mixin;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotPosAccess {
    @Accessor("x")
    @Mutable
    int getX();

    @Accessor("x")
    @Mutable
    void setX(int value);

    @Accessor("y")
    @Mutable
    int getY();

    @Accessor("y")
    @Mutable
    void setY(int value);
}

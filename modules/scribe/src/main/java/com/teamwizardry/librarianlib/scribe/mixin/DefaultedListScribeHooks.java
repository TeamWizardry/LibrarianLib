package com.teamwizardry.librarianlib.scribe.mixin;

import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(DefaultedList.class)
public interface DefaultedListScribeHooks {
    @Accessor("delegate")
    List<?> getDelegate();

    @Mutable
    @Accessor("delegate")
    void setDelegate(List<?> value);
}

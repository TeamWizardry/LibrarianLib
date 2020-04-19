package com.teamwizardry.librarianlib.core.mixin;

import com.teamwizardry.librarianlib.core.bridge.MixinEnvCheckTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MixinEnvCheckTarget.class, remap = false)
public class MixinEnvCheck {
    /**
     * @author LibrarianLib
     * @reason To verify that Mixins are being applied at runtime
     */
    @Overwrite
    public boolean isPatched() {
        return true;
    }
}

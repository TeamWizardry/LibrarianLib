package com.teamwizardry.librarianlib.facade.mixin;

import com.teamwizardry.librarianlib.facade.bridge.FacadeContainerScreenHooks;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class ContainerScreenMixin {
    @Inject(method = "isPointOverSlot", at = @At("RETURN"), cancellable = true)
    public void isSlotSelectedHook(Slot slotIn, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if(this instanceof FacadeContainerScreenHooks) {
            FacadeContainerScreenHooks hooks = (FacadeContainerScreenHooks) this;
            cir.setReturnValue(hooks.isSlotSelectedHook(slotIn, mouseX, mouseY, cir.getReturnValueZ()));
        }
    }
}

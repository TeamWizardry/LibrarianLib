package com.teamwizardry.librarianlib.lieutenant.mixin;

import com.teamwizardry.librarianlib.lieutenant.ClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientSuggestionProvider.class)
public abstract class ClientSuggestionProviderMixin implements ClientCommandSource {
    @Shadow
    @Final
    private Minecraft mc;

    @Override
    public void logFeedback(@NotNull ITextComponent message) {
        mc.player.sendStatusMessage(message, false);
    }

    @Override
    public void sendFeedback(@NotNull ITextComponent message, boolean actionBar) {
        mc.player.sendStatusMessage(message, actionBar);
    }

    @Override
    public void sendErrorMessage(@NotNull ITextComponent text) {
        mc.player.sendStatusMessage(new StringTextComponent("").append(text).mergeStyle(TextFormatting.RED), false);
    }
}

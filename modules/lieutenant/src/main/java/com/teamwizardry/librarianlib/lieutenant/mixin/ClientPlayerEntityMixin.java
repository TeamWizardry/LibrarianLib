package com.teamwizardry.librarianlib.lieutenant.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamwizardry.librarianlib.lieutenant.bridge.ClientCommandCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.teamwizardry.librarianlib.core.util.CoreUtils.mixinCast;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow
    @Final
    public ClientPlayNetHandler connection;

    @Shadow
    public abstract void sendStatusMessage(ITextComponent chatComponent, boolean actionBar);

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChatMessage(String msg, CallbackInfo info) {
        if (msg.length() < 2 || !msg.startsWith("/")) return;
        if (!ClientCommandCache.INSTANCE.hasCommand(msg.substring(1).split(" ")[0])) return;
        boolean cancel = false;
        try {
            // The game freezes when using heavy commands. Run your heavy code somewhere else pls
            int result = ClientCommandCache.INSTANCE.execute(
                    msg.substring(1), mixinCast(new ClientSuggestionProvider(connection, mc))
            );
            if (result != 0)
                // Prevent sending the message
                cancel = true;
        } catch (CommandException e) {
            sendStatusMessage(e.getComponent().applyTextStyle(TextFormatting.RED), false);
            cancel = true;
        } catch (CommandSyntaxException e) {
            sendStatusMessage(new StringTextComponent(e.getMessage()).applyTextStyle(TextFormatting.RED), false);
            cancel = true;
        } catch (Exception e) {
            sendStatusMessage(new TranslationTextComponent("command.failed").applyTextStyle(TextFormatting.RED), false);
            cancel = true;
        }

        if (cancel)
            info.cancel();
    }
}

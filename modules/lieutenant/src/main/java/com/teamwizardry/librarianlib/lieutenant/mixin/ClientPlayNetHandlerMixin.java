package com.teamwizardry.librarianlib.lieutenant.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.teamwizardry.librarianlib.lieutenant.RegisterClientCommandsEvent;
import com.teamwizardry.librarianlib.lieutenant.bridge.ClientCommandCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.teamwizardry.librarianlib.core.util.CoreUtils.mixinCast;

@Mixin(ClientPlayNetHandler.class)
public abstract class ClientPlayNetHandlerMixin {
    @Shadow
    private CommandDispatcher<ISuggestionProvider> commandDispatcher;

    @Inject(method = "handleCommandList", at = @At("RETURN"))
    private void handleCommandList(SCommandListPacket packetIn, CallbackInfo ci) {
        addCommands();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(Minecraft mcIn, Screen previousGuiScreen, NetworkManager networkManagerIn, GameProfile profileIn, CallbackInfo ci) {
        addCommands();
        ClientCommandCache.INSTANCE.build();
    }

    @Unique
    private void addCommands() {
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(mixinCast(commandDispatcher)));
    }
}

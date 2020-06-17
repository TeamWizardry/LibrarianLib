package com.teamwizardry.librarianlib.core.mixin;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.core.bridge.IRenderTypeState;
import com.teamwizardry.librarianlib.core.bridge.MixinEnvCheckTarget;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.ArrayList;

@Mixin(RenderType.State.class)
public abstract class RenderTypeStateMixin implements IRenderTypeState {
    @Override
    public void addState(RenderState state) {
        ArrayList<RenderState> states = new ArrayList<>(getRenderStates());
        states.add(state);
        setRenderStates(ImmutableList.copyOf(states));
    }

    @Accessor
    @Mutable
    @Override
    public abstract ImmutableList<RenderState> getRenderStates();

    @Accessor
    @Mutable
    @Override
    public abstract void setRenderStates(ImmutableList<RenderState> renderStates);
}

package com.teamwizardry.librarianlib.core.mixin;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderLayerPhaseParameters;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(RenderLayer.MultiPhaseParameters.class)
abstract class RenderLayerPhaseParametersMixin implements IMutableRenderLayerPhaseParameters {
    @Override
    public void addPhase(RenderPhase state) {
        ArrayList<RenderPhase> states = new ArrayList<>(this.getPhases());
        states.add(state);
        this.setPhases(ImmutableList.copyOf(states));
    }

    @Accessor
    @Mutable
    @Override
    public abstract ImmutableList<RenderPhase> getPhases();

    @Accessor
    @Mutable
    @Override
    public abstract void setPhases(ImmutableList<RenderPhase> RenderPhases);
}

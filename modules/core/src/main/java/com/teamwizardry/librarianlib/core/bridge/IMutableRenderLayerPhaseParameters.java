package com.teamwizardry.librarianlib.core.bridge;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderPhase;

public interface IMutableRenderLayerPhaseParameters {
    void addState(RenderPhase state);
    default void addState(String name, Runnable setupTask, Runnable clearTask) {
        this.addState(new RenderPhase(name, setupTask, clearTask) {});
    }
    ImmutableList<RenderPhase> getPhases();
    void setPhases(ImmutableList<RenderPhase> RenderPhases);
}

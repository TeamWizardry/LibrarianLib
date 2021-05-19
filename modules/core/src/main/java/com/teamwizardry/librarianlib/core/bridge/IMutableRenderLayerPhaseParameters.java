package com.teamwizardry.librarianlib.core.bridge;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderPhase;

public interface IMutableRenderLayerPhaseParameters {
    void addPhase(RenderPhase state);
    default void addPhase(String name, Runnable setupTask, Runnable clearTask) {
        this.addPhase(new RenderPhase(name, setupTask, clearTask) {});
    }
    ImmutableList<RenderPhase> getPhases();
    void setPhases(ImmutableList<RenderPhase> RenderPhases);
}

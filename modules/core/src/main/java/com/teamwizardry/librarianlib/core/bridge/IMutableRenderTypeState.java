package com.teamwizardry.librarianlib.core.bridge;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderState;

public interface IMutableRenderTypeState {
    void addState(RenderState state);
    default void addState(String name, Runnable setupTask, Runnable clearTask) {
        this.addState(new RenderState(name, setupTask, clearTask) {});
    }
    ImmutableList<RenderState> getRenderStates();
    void setRenderStates(ImmutableList<RenderState> renderStates);
}

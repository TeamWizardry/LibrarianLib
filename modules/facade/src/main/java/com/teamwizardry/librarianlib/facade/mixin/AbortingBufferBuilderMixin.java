package com.teamwizardry.librarianlib.facade.mixin;

import com.teamwizardry.librarianlib.facade.bridge.AbortingBufferBuilder;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormatElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class AbortingBufferBuilderMixin implements AbortingBufferBuilder {
    @Shadow private boolean building;

    @Shadow private int vertexCount;

    @Shadow @Nullable private VertexFormatElement currentElement;

    @Shadow private int currentElementId;

    @Shadow public abstract void reset();

    public void abort() {
        this.building = false;
        this.vertexCount = 0;
        this.currentElement = null;
        this.currentElementId = 0;
        this.reset();
    }
}

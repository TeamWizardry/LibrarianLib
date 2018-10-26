package com.teamwizardry.librarianlib.features.particlesystem.bindings;

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding;
import org.jetbrains.annotations.NotNull;

public class CallbackBinding implements ReadParticleBinding {
    private final int size;
    private final Callback callback;

    public CallbackBinding(int size, Callback callback) {
        this.size = size;
        this.callback = callback;
    }

    @Override
    public int getSize() {
        return size;
    }


    @Override
    public double get(@NotNull double[] particle, int index) {
        return callback.call(particle, index);
    }

    @FunctionalInterface
    public interface Callback {
        double call(@NotNull double[] particle, int index);
    }
}

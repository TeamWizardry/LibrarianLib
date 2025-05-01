package com.teamwizardry.librarianlib.glitter.bindings;

import com.teamwizardry.librarianlib.glitter.ReadParticleBinding;
import org.jetbrains.annotations.NotNull;

public class CallbackBinding implements ReadParticleBinding {
    private final double[] contents;
    private final Callback callback;

    public CallbackBinding(int size, Callback callback) {
        this.contents = new double[size];
        this.callback = callback;
    }

    @NotNull
    @Override
    public double @NotNull [] getContents() {
        return contents;
    }

    @Override
    public void load(double @NotNull [] particle) {
        callback.call(particle, contents);
    }

    @FunctionalInterface
    public interface Callback {
        void call(double @NotNull [] particle, double @NotNull [] contents);
    }
}

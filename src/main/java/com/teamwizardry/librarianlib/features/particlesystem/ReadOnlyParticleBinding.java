package com.teamwizardry.librarianlib.features.particlesystem;

public interface ReadOnlyParticleBinding extends ParticleBinding {
    @Override
    default void set(double[] particle, int index, double value) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only");
    }
}

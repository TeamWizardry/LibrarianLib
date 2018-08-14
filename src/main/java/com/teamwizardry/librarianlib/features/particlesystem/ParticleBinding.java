package com.teamwizardry.librarianlib.features.particlesystem;

public interface ParticleBinding {
    /**
     * The number of components in this binding, or -1 if it is unbounded
     */
    int getSize();
    double get(double[] particle, int index);
    void set(double[] particle, int index, double value);
}


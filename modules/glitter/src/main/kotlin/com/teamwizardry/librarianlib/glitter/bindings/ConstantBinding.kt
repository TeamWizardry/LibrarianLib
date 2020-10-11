package com.teamwizardry.librarianlib.glitter.bindings

import com.teamwizardry.librarianlib.glitter.ReadParticleBinding

/**
 * A read-only binding backed by a constant value.
 */
public class ConstantBinding(
    /**
     * The array of elements to use.
     */
    private vararg val constantContents: Double
): ReadParticleBinding {
    override val contents: DoubleArray = DoubleArray(constantContents.size)

    override fun load(particle: DoubleArray) {
        constantContents.copyInto(contents)
    }
}
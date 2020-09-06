package com.teamwizardry.librarianlib.glitter.modules

import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.WriteParticleBinding
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule

/**
 * Copies the elements from one binding to another.
 *
 * Performs a single operation, copies the each element from [source] into [target].
 */
public class SetValueUpdateModule(
    /**
     * The target binding, where [source] will be copied into. The size of the target cannot be indefinite (-1)
     */
    @JvmField public val target: WriteParticleBinding,
    /**
     * The source binding, what will be copied into [target]. The size of this binding must be equal to the size of
     * [target] or be indefinite.
     */
    @JvmField public val source: ReadParticleBinding
): ParticleUpdateModule {
    init {
        if (source.contents.size != target.contents.size) {
            throw IllegalArgumentException("Source binding has a non-indefinite size which is different from the " +
                "target size. source: ${source.contents.size} target: ${target.contents.size}")
        }
    }

    override fun update(particle: DoubleArray) {
        source.load(particle)
        source.contents.copyInto(target.contents)
        target.store(particle)
    }
}
package com.teamwizardry.librarianlib.features.particlesystem

import com.teamwizardry.librarianlib.features.particlesystem.bindings.StoredBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.VariableBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.DepthSortModule
import org.magicwerk.brownies.collections.GapList
import java.util.*

/**
 * A system of particles with similar behavior.
 *
 * Particle systems define particle behavior through "modules", of which there are multiple kinds:
 *
 * * [updateModules] are called in every tick for each particle. They are called in sequence for each particle, meaning
 * any state set in one can be safely used in the next.
 * * [postUpdateModules] are called every tick and passed the full list of particles. They are generally used for depth
 * sorting (see [DepthSortModule]) however they are useful for anything that requires the full particle list in one call.
 * * [renderPrepModules] are similar to [updateModules] but are called before each particle is rendered. They can be
 * used to initialize [VariableBinding]s so a complex computation does not have to be repeated in several of the render
 * module's input bindings.
 * * [renderModules] are in charge of actually rendering the particles into the world. You can add multiple and each
 * will render in order.
 *
 * The various module lists may be safely modified as long as the system isn't currently ticking or rendering, however
 * once any particles have been created it is no longer safe to create new bindings with [bind].
 *
 * When creating particle systems it is highly recommended to spawn enough to cause a moderate amount of lag and run
 * profiling to find which hotspots are slowing down the game. It is often surprising what small things have a large
 * impact.
 *
 * When creating a particle system it is recommended to create a singleton subclass. That subclass can manage the
 * initialization of the system as well as provide a specialized wrapper around [addParticle] that allows more
 * meaningful arguments to be passed. For example, such a method might look something like this:
 *
 * ```java
 * double[] addParticle(double lifetime, Vec3d position, Vec3d velocity, Color color, double size) {
 *     return this.addParticle(lifetime,
 *             position.x, position.y, position.z, // position
 *             position.x, position.y, position.z, // previousPosition
 *             velocity.x, velocity.y, velocity.z, // velocity
 *             color.r/255.0, color.g/255.0, color.b/255.0, color.a/255.0, // color
 *             size // size
 *     )
 * }
 * ```
 */
open class ParticleSystem {
    /**
     * The modules that are called every tick for each particle. They are called in sequence for each particle, meaning
     * temporary state set in one module can be safely used in the subsequent modules.
     */
    val updateModules: MutableList<ParticleUpdateModule> = mutableListOf()
    /**
     * The modules that are called once at the end of each tick and passed the entire list of particles. Often used for
     * depth sorting
     */
    val postUpdateModules: MutableList<ParticleBatchUpdateModule> = mutableListOf()
    /**
     * The modules that are called before each particle is rendered. They are similar to [updateModules] and are often
     * used to optimize computations that are reused in multiple places (say, calculating a normal vector) by storing
     * them in a local variable or [VariableBinding]. These will be called individually by each renderer, so they cannot
     * be used to optimize computations that are reused between renderers.
     */
    val renderPrepModules: MutableList<ParticleUpdateModule> = mutableListOf()
    /**
     * The modules that handle the rendering of the particles in this system. They are each passed the full list of
     * particles, and are free to use that information however they please. They are called in sequence, meaning any
     * depth sorting will only work within the confines of a single module. For example, depth sorting and having two
     * translucent renderer modules would result in the first module rendering, then the translucent particles in
     * the second module being occluded by any polygons the first renders.
     */
    val renderModules: MutableList<ParticleRenderModule> = mutableListOf()

    /**
     * The maximum number of particles in the particle reuse pool. The reuse pool is used to reduce the amount of memory
     * churn by retaining dead particle arrays and reusing them in [addParticle] as opposed to creating new ones each
     * time.
     */
    var poolSize = 1000

    internal val particles: MutableList<DoubleArray> = GapList<DoubleArray>()
    private val particlePool = ArrayDeque<DoubleArray>(poolSize)

    /**
     * The built-in binding for particle lifetime. If the value in [age] is >= the value in [lifetime] the particle will
     * be removed from the world.
     */
    val lifetime = StoredBinding(0, 1)
    /**
     * The built-in binding for particle age. Upon spawning the value in this binding is initialized to 0, and very
     * tick thereafter its value is incremented. If the value in [age] is >= the value in [lifetime] the particle will
     * be removed from the world.
     */
    val age = StoredBinding(1, 1)
    /**
     * The required length of the particle arrays.
     */
    var fieldCount = 2
        private set

    /**
     * Creates a new [StoredBinding] of the specified size and allocates space for it at the end of the particle array, increasing [fieldCount] to reflect the change.
     *
     * @throws IllegalStateException if particles have already been created by this system, rendering it unsafe to create new bindings
     */
    fun bind(size: Int): StoredBinding {
        if(particles.isNotEmpty() || particlePool.isNotEmpty())
            throw IllegalStateException("It is no longer safe to create new bindings, particles have already been " +
                    "created based on current field count.")
        val binding = StoredBinding(fieldCount, size)
        fieldCount += size
        return binding
    }

    /**
     * Creates a particle and initializes it with the passed values. Any missing values will be set to 0.
     *
     * [params] should be populated with the values for each binding the order they were bound. For example, if
     * a `position` binding was created with a size of 3, then a `color` with a size of 4, and then a `size` with a
     * size of 1, [params] should be populated in the following order: `x, y, z, r, g, b, a, size`. It is recommended
     * that a subclass be created with a method that populates [params] based upon more meaningfully named arguments.
     *
     * @param lifetime the lifetime of the particle in ticks
     * @param params an array of values to initialize the particle array with.
     */
    fun addParticle(lifetime: Double, vararg params: Double): DoubleArray {
        val particle = DoubleArray(fieldCount)
        particle[0] = lifetime
        particle[1] = 0.0
        (2 until particle.size).forEach { i ->
            if(i-2 < params.size)
                particle[i] = params[i-2]
            else
                particle[i] = 0.0
        }
        particles.add(particle)
        return particle
    }

    internal fun update() {
        val iter = particles.iterator()
        for(particle in iter) {
            val lifetime = this.lifetime[particle, 0]
            val age = this.age[particle, 0]
            if(age >= lifetime) {
                iter.remove()
                if(particlePool.size < poolSize)
                    particlePool.push(particle)
                continue
            }
            this.age[particle, 0] = age + 1
            update(particle)
        }

        for(i in 0 until postUpdateModules.size) {
            postUpdateModules[i].update(particles)
        }
    }

    private fun update(particle: DoubleArray) {
        for(i in 0 until updateModules.size) {
            updateModules[i].update(particle)
        }
    }

    internal fun render() {
        for(i in 0 until renderModules.size) {
            renderModules[i].render(particles, renderPrepModules)
        }
    }
}

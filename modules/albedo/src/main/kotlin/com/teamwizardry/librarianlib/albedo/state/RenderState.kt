package com.teamwizardry.librarianlib.albedo.state

import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableCopy
import net.minecraft.client.render.RenderPhase
import net.minecraft.util.Identifier

/**
 * An encapsulation of an OpenGL state. This includes things like the blending flag, blend function, culling, depth
 * tests, etc.
 *
 * Unlike Mojang's [RenderPhase], this class does not include the bound texture. The texture is a uniform, which is
 * part of the shader, and thus is the responsibility of that shader's [RenderBuffer].
 */
public class RenderState(parameters: Map<Identifier, State>) {
    public val parameters: Map<Identifier, State> = parameters.unmodifiableCopy()

    /**
     * Apply this state
     */
    public fun apply() {
        for(parameter in parameters.values) {
            parameter.apply()
        }
    }

    /**
     * Cleans up any state that requires cleanup. This *shouldn't* be necessary, but largely due to Mojang's garbage
     * code, it is mandatory to mitigate state leaks. (state leaks are a fact of life in Minecraft,
     *
     * Unlike Mojang's state management code, this doesn't "undo" the changes made by this state, it simply provides an
     * opportunity for the state parameters to perform any necessary cleanup.
     */
    public fun cleanup() {
        for(parameter in parameters.values) {
            parameter.apply()
        }
    }

    /**
     * Create a new render state based on this one.
     */
    public fun extend(): Builder {
        return Builder(parameters.toMutableMap())
    }

    /**
     * Create a new render state based on this one.
     */
    public fun extend(vararg extra: State): RenderState {
        val builder = Builder(parameters.toMutableMap())
        for(state in extra) {
            builder.add(state)
        }
        return builder.build()
    }

    public abstract class State(
        /**
         * The name identifying this parameter
         */
        public val parameter: Identifier
    ) {
        public abstract fun apply()
        public open fun cleanup() {}
    }

    public companion object {
        @JvmStatic
        public val normal: RenderState = builder()
            .add(DefaultRenderStates.Blend.DISABLED)
            .add(DefaultRenderStates.Cull.ENABLED)
            .add(DefaultRenderStates.DepthTest.LEQUAL)
            .add(DefaultRenderStates.WriteMask.ENABLED)
            .build()

        @JvmStatic
        public fun builder(): Builder {
            return Builder(mutableMapOf())
        }
    }

    public class Builder(public val parameters: MutableMap<Identifier, State>) {
        public fun add(state: State): Builder = build { parameters[state.parameter] = state }
        public fun remove(parameter: Identifier): Builder = build { parameters.remove(parameter) }
        public fun build(): RenderState = RenderState(parameters)

        private inline fun build(crossinline block: () -> Unit): Builder {
            block()
            return this
        }
    }
}
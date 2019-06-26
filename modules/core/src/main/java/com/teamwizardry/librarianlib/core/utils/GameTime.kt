package com.teamwizardry.librarianlib.core.utils

import com.teamwizardry.mirror.Mirror
import net.minecraft.client.Minecraft
import net.minecraft.util.Timer
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Convenient access to ticks, partial ticks, interpolation, etc.
 */
abstract class GameTime {

    /**
     * The number of ticks that have been counted since this timer began.
     */
    abstract val time: Int

    /**
     * The progress through the current tick. Value ranges from [0-1)
     */
    abstract val partialTicks: Float

    fun interpolate(previousValue: Double, value: Double): Double {
        return previousValue + (value - previousValue) * partialTicks
    }

    companion object {
        private val timer: Timer = Mirror.reflectClass<Minecraft>()
            .let { it.declaredField("timer") ?: it.declaredField("field_71428_T")!! }
            .get(Minecraft.getInstance())

        private val _global = object: GameTime() {
            override var time: Int = 0
            override val partialTicks: Float
                get() = timer.renderPartialTicks
        }

        private val _world = object: GameTime() {
            private val renderPartialTicksPaused = Mirror.reflectClass<Minecraft>()
                .let { it.declaredField("renderPartialTicksPaused") ?: it.declaredField("field_193996_ah")!! }
            override var time: Int = 0
            override val partialTicks: Float
                get() = renderPartialTicksPaused.get(Minecraft.getInstance())
        }

        /**
         * The global game timer. Use [world] for anything that should pause when the game does (such as anything that
         * renders in the world)
         */
        @JvmField
        val global: GameTime = _global

        @JvmField
        val world: GameTime = _world

        @JvmStatic
        @SubscribeEvent
        fun clientTickEnd(event: TickEvent.ClientTickEvent) {
            if (event.phase == TickEvent.Phase.END) {
                val mc = Minecraft.getInstance()
                if(!mc.isGamePaused)
                    _world.time += timer.elapsedTicks
                _global.time += timer.elapsedTicks
            }
        }
    }
}

package com.teamwizardry.librarianlib.core.util

import com.teamwizardry.librarianlib.math.Vec2d
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.render.Tessellator
import net.minecraft.client.texture.TextureManager
import net.minecraft.client.util.Window
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture

public object Client {
    @JvmStatic
    public val minecraft: MinecraftClient
        get() = MinecraftClient.getInstance()

    @JvmStatic
    public val player: ClientPlayerEntity?
        get() = minecraft.player

    @JvmStatic
    public val window: Window
        get() = minecraft.window

    @JvmStatic
    public val scaleFactor: Double
        get() = window.scaleFactor

    @JvmStatic
    public val resourceManager: ResourceManager
        get() = minecraft.resourceManager

    @JvmStatic
    public val textureManager: TextureManager
        get() = minecraft.textureManager

    @JvmStatic
    public val textRenderer: TextRenderer
        get() = minecraft.textRenderer

    @JvmStatic
    public val tessellator: Tessellator
        get() = Tessellator.getInstance()

    /**
     * The game time, as measured from the game launch
     */
    @JvmStatic
    public val time: GameTime = GameTime()

    /**
     * The world time, as measured from the game launch. This timer pauses when the game is paused.
     */
    @JvmStatic
    public val worldTime: GameTime = GameTime()

    @JvmStatic
    public fun openScreen(screen: Screen?) {
        minecraft.setScreen(screen)
    }

    public class GameTime {
        public var ticks: Int = 0
            private set
        public var tickDelta: Float = 0f
            private set

        public val time: Float
            get() = ticks + tickDelta
        public val seconds: Float
            get() = time / 20

        public fun interp(previous: Double, current: Double): Double {
            return previous + (current - previous) * tickDelta
        }

        @Suppress("NOTHING_TO_INLINE")
        public inline fun interp(previous: Number, current: Number): Double = interp(previous.toDouble(), current.toDouble())

        public fun interp(previous: Vec2d, current: Vec2d): Vec2d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y))
        }

        public fun interp(previous: Vec3d, current: Vec3d): Vec3d {
            return vec(interp(previous.x, current.x), interp(previous.y, current.y), interp(previous.z, current.z))
        }

        public fun updateTime(ticks: Int, tickDelta: Float) {
            this.ticks = ticks
            this.tickDelta = tickDelta
        }

        public fun trackTick() {
            ticks++
        }

        public fun updateTickDelta(tickDelta: Float) {
            this.tickDelta = tickDelta
        }
    }
}

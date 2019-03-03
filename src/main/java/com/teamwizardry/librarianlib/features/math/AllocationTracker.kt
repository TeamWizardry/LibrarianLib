package com.teamwizardry.librarianlib.features.math

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object AllocationTracker {
    var vec2dAllocations = 0L
    var vec2dPooledAllocations = 0L
    var rect2dAllocations = 0L
    var vec3dAllocations = 0L
    var vec3dPooledAllocations = 0L

    val vec2dSize = 56
    val vec3dSize = 40
    val rect2dSize = 80
}

object AllocationDisplay {
    init { MinecraftForge.EVENT_BUS.register(this) }

    val vec2dAllocations = AllocationWindow()
    val vec2dPooledAllocations = AllocationWindow()
    val rect2dAllocations = AllocationWindow()
    val vec3dAllocations = AllocationWindow()
    val vec3dPooledAllocations = AllocationWindow()

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        vec2dAllocations.sample(AllocationTracker.vec2dAllocations)
        vec2dPooledAllocations.sample(AllocationTracker.vec2dPooledAllocations)
        rect2dAllocations.sample(AllocationTracker.rect2dAllocations)
        vec3dAllocations.sample(AllocationTracker.vec3dAllocations)
        vec3dPooledAllocations.sample(AllocationTracker.vec3dPooledAllocations)
    }

    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return
        event.left.add("LibrarianLib Allocations:")
        event.left.add(line("Vec2d", vec2dAllocations.average, vec2dPooledAllocations.average, AllocationTracker.vec2dSize))
        event.left.add(line("Rect2d", rect2dAllocations.average, null, AllocationTracker.rect2dSize))
    }

    fun line(name: String, allocations: Double, pooled: Double?, size: Int): String {
        var line = " - $name:"
        line += " ${allocations.toInt()} new/s"
        if(pooled != null) {
            val percent = 100 * pooled / (allocations + pooled)
            line += " (${percent.toInt()}% pooled)"
        }
        val memory = size * allocations
        line += when {
            memory > B_PER_MB -> " - %.2f MiB/s".format(memory/B_PER_MB)
            memory > B_PER_KB -> " - %.2f KiB/s".format(memory/B_PER_KB)
            else -> " - %d bytes/s".format(memory.toInt())
        }
        return line
    }

    private val B_PER_KB = 1024
    private val B_PER_MB = 1024 * 1024
}

class AllocationWindow {
    var windowSize = 5000

    val values = LongArrayFIFOQueue()
    val times = LongArrayFIFOQueue()

    fun sample(value: Long) {
        val now = System.currentTimeMillis()
        val cutoff = now-windowSize
        while(!times.isEmpty && times.firstLong() < cutoff) {
            times.dequeueLong()
            values.dequeueLong()
        }
        times.enqueue(now)
        values.enqueue(value)
    }

    val average: Double
        get() {
            if(values.isEmpty) return 0.0
            val timeDelta = times.lastLong() - times.firstLong()
            if(timeDelta == 0L) return 0.0
            val valueDelta = values.lastLong() - values.firstLong()
            return 1000 * valueDelta / timeDelta.toDouble()
        }
}

package com.teamwizardry.librarianlib.features.math

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import java.io.FileNotFoundException

object AllocationTracker {
    var vec2dAllocations = 0L
    var vec2dPooledAllocations = 0L
    var vec2dAllocationStats: Object2IntMap<Vec2d>? = null
    var rect2dAllocations = 0L
    var rect2dAllocationStats: Object2IntMap<Rect2d>? = null
    var vec3dAllocations = 0L
    var vec3dPooledAllocations = 0L
    var vec3dAllocationStats: Object2IntMap<Vec3d>? = null

    val vec2dSize = 32
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
    var statsStartTime: Int = 0
    var statsEndTime: Int? = null
    var statsOutputDir: String = "~"

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        vec2dAllocations.sample(AllocationTracker.vec2dAllocations)
        vec2dPooledAllocations.sample(AllocationTracker.vec2dPooledAllocations)
        rect2dAllocations.sample(AllocationTracker.rect2dAllocations)
        vec3dAllocations.sample(AllocationTracker.vec3dAllocations)
        vec3dPooledAllocations.sample(AllocationTracker.vec3dPooledAllocations)
        if(statsEndTime?.let { ClientTickHandler.ticks >= it } == true) {
            endStats()
            statsEndTime = null
        }
    }

    /**
     * Pause execution in your debugger and use direct execution to call this when you want to start collecting stats.
     * If a number of ticks is passed for [duration], the stats will automatically stop after that time
     */
    fun startStats(duration: Int?, outputDir: String = "~") {
        AllocationTracker.vec2dAllocationStats = Object2IntOpenHashMap()
        AllocationTracker.rect2dAllocationStats = Object2IntOpenHashMap()
        AllocationTracker.vec3dAllocationStats = Object2IntOpenHashMap()
        statsOutputDir = outputDir
        statsEndTime = duration?.let { ClientTickHandler.ticks + it }
    }

    /**
     * Pause execution in your debugger and use direct execution to call this when you want to stop collecting stats.
     * Pass the output directory for the stat csv files
     */
    fun endStats() {
        try {
            File("$statsOutputDir").mkdirs()
            val duration = (ClientTickHandler.ticks - statsStartTime) / 20.0
            AllocationTracker.vec2dAllocationStats?.also { vec2dStats ->
                File("$statsOutputDir/vec2d-${duration.toInt()}s.csv").bufferedWriter().use { file ->
                    file.write("X,Y,Count\n")
                    vec2dStats.object2IntEntrySet().forEach {
                        file.write("${it.key.x},${it.key.y},${it.intValue}\n")
                    }
                }
            }

            AllocationTracker.rect2dAllocationStats?.also { rect2dStats ->
                File("$statsOutputDir/rect2d-${duration.toInt()}s.csv").bufferedWriter().use { file ->
                    file.write("X,Y,Width,Height,Count\n")
                    rect2dStats.object2IntEntrySet().forEach {
                        file.write("${it.key.x},${it.key.y},${it.key.width},${it.key.height},${it.intValue}\n")
                    }
                }
            }

            AllocationTracker.vec3dAllocationStats?.also { vec3dStats ->
                File("$statsOutputDir/vec3d-${duration.toInt()}s.csv").bufferedWriter().use { file ->
                    file.write("X,Y,Z,Count\n")
                    vec3dStats.object2IntEntrySet().forEach {
                        file.write("${it.key.x},${it.key.y},${it.key.z},${it.intValue}")
                    }
                }
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
        }

        AllocationTracker.vec2dAllocationStats = null
        AllocationTracker.rect2dAllocationStats = null
        AllocationTracker.vec3dAllocationStats = null
    }

    @SubscribeEvent
    fun debug(event: RenderGameOverlayEvent.Text) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return
        event.left.add("LibrarianLib Allocations:")
        line(event.left, "Vec3d", vec3dAllocations.average, vec3dPooledAllocations.average, AllocationTracker.vec3dSize, AllocationTracker.vec3dAllocationStats)
        line(event.left, "Vec2d", vec2dAllocations.average, vec2dPooledAllocations.average, AllocationTracker.vec2dSize, AllocationTracker.vec2dAllocationStats)
        line(event.left, "Rect2d", rect2dAllocations.average, null, AllocationTracker.rect2dSize, AllocationTracker.rect2dAllocationStats)
    }

    fun line(list: MutableList<String>, name: String, allocations: Double, pooled: Double?, size: Int, stats: Object2IntMap<*>?) {
        if(allocations == 0.0 && (pooled == null || pooled == 0.0) && stats.isNullOrEmpty())
            return
        var line = " - $name:"
        line += " ${allocations.toInt()} unpooled/s"
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
        if(stats != null) {
            line += " - Stats: ${stats.size} unique allocations"
        }
        list.add(line)
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

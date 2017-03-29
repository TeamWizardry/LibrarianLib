package com.teamwizardry.librarianlib.features.particle

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.math.interpolate.InterpListGenerator
import net.minecraft.client.Minecraft
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer

/**
 * Manages the spawning of particles along paths
 */
object ParticleSpawner {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    /**
     * The particles that are pending spawning
     */
    private var pending: MutableSet<ParticleSpawn> = Collections.newSetFromMap(ConcurrentHashMap())

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun tickEvent(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) {
            val mc = Minecraft.getMinecraft()
            val gui = mc.currentScreen
            if (gui == null || !gui.doesGuiPauseGame()) {
                tick()
            }
        }
    }

    fun tick() {
        pending.forEach { if (it.ticksTillSpawn <= 0) ParticleRenderManager.spawn(it.particle) }
        pending.removeAll { it.ticksTillSpawn-- <= 0 }
    }

    /**
     * Spawn [particleCount] particles along [curve], taking [travelTime] ticks to spawn them.
     *
     * [callback] is called before each particle is created, and is supplied with the value from 0-1 along [curve] and
     * the builder itself. Allows setings to be changed for each particle. All particles are created immediately and
     * spawned at varied times according to [travelTime].
     */
    @JvmStatic
    @JvmOverloads
    fun spawn(builder: ParticleBuilder, world: World, curve: InterpFunction<Vec3d>, particleCount: Int, travelTime: Int = 0, callback: BiConsumer<Float, ParticleBuilder> = BiConsumer { t, u -> }) {
        val actualParticleCount = modifyParticleCount(particleCount)

        InterpListGenerator.getIndexList(actualParticleCount).forEach { t ->
            val tick = Math.floor(t * travelTime.toDouble()).toInt()
            callback.accept(t, builder)
            val particle = builder.build(world, curve.get(t))
            if (particle != null)
                pending.add(ParticleSpawn(particle, tick))
        }
    }

    private fun modifyParticleCount(particleCount: Int): Int {
        val mul: Float =
                when (Minecraft.getMinecraft().gameSettings.particleSetting) {
                    0 -> 1f
                    1 -> 0.5f
                    2 -> 0.25f
                    else -> 1f
                }
        return Math.max(2f, particleCount.toFloat() * mul).toInt()
    }


}

private data class ParticleSpawn private constructor(val particle: ParticleBase) {
    var ticksTillSpawn = 0

    constructor(particle: ParticleBase, ticksTillSpawn: Int) : this(particle) {
        this.ticksTillSpawn = ticksTillSpawn
    }
}

/**
 * Kotlin wrapper for [spawn]. Uses kotlin lambdas instead of SAM classes
 */
fun ParticleSpawner.spawn(builder: ParticleBuilder, world: World, curve: InterpFunction<Vec3d>, particleCount: Int, travelTime: Int = 0, callback: (Float, ParticleBuilder) -> Unit = { a, b -> }) {
    spawn(builder, world, curve, particleCount, travelTime, BiConsumer<Float, ParticleBuilder>(callback))
}

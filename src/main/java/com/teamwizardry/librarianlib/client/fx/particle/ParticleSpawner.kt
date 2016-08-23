package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Created by TheCodeWarrior
 */
object ParticleSpawner {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    private val pending: MutableSet<ParticleSpawn> = mutableSetOf()

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
        val effectRenderer = Minecraft.getMinecraft().effectRenderer

        pending.forEach { it.ticksTillSpawn-- }

        pending.forEach { if( it.ticksTillSpawn == 0 ) effectRenderer.addEffect(it.particle) }

        pending.removeAll { it.ticksTillSpawn == 0 }
    }

    @JvmOverloads
    fun spawn(builder: ParticleBuilder, world: World, curve: InterpFunction<Vec3d>, particleCount: Int, travelTime: Int = 0, callback: BiConsumer<Float, ParticleBuilder> = noop) {
        val particleSpan = if(particleCount == 0) 0f else 1f/(particleCount-1)
        for (i in 0..particleCount-1) {
            val t = particleSpan*i
            val tick = Math.floor(t*travelTime.toDouble()).toInt()
            callback.accept(t, builder)
            val particle = builder.build(world, curve.get(t))
            if(particle != null)
                pending.add(ParticleSpawn(particle, tick))
        }
    }

    private val noop = BiConsumer<Float, ParticleBuilder> { a, b -> }
}

private data class ParticleSpawn(val particle: ParticleBase, var ticksTillSpawn: Int)
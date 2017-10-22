package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.util.Timer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

/**
 * TODO: Document file Animator
 *
 * Created by TheCodeWarrior
 */
val Minecraft.renderPartialTicksPaused by MethodHandleHelper.delegateForReadOnly<Minecraft, Float>(Minecraft::class.java, "renderPartialTicksPaused", "field_193996_ah")
val Minecraft.timer by MethodHandleHelper.delegateForReadOnly<Minecraft, Timer>(Minecraft::class.java, "timer", "field_71428_T")

class Animator {

    init {
        animators.add(this)
    }

    var deletePastAnimations = true
    var useWorldPartialTicks = false
    private fun partialTicks() =
            if(useWorldPartialTicks)
                worldPartialTicks
            else
                screenPartialTicks

    private var timeOffset: Float = partialTicks()

    var time: Float
        get() = partialTicks()*speed - timeOffset
        set(value) {
            timeOffset = partialTicks()*speed - value
        }

    var speed: Float = 1f
        get() = field
        set(value) {
            timeOffset = time + partialTicks()*value
            field = value
        }

    // sorted in ascending start order so I can quickly cull the expired animations and efficiently queue large numbers
    // of animations without having to iterate over them
    private val animations: MutableSet<Animation<*>> = TreeSet(compareBy { it.start })
    private val currentAnimations = mutableListOf<Animation<*>>()

    fun add(animation: Animation<*>) {
        animations.add(animation)
        animation.onAddedToAnimator(this)
    }

    fun update() {
        updateCurrentAnimations()

        currentAnimations.forEach { anim ->
            anim.update(time)
        }
    }

    private fun updateCurrentAnimations() {
        val time = this.time

        currentAnimations.clear()

        var toDelete: MutableSet<Animation<*>>? = null
        for(animation in animations) {
            if(animation.end < time && deletePastAnimations) {
                if(toDelete == null) toDelete = mutableSetOf()
                toDelete.add(animation)
                continue;
            }
            if(animation.start > time) break;

            currentAnimations.add(animation)
        }

        toDelete?.forEach { it.update(time) }

        if(toDelete != null) animations.removeAll(toDelete)
    }

    companion object {
        @JvmStatic
        val screenPartialTicks: Float
            get() = screenTicks + Minecraft.getMinecraft().timer.renderPartialTicks

        @JvmStatic
        val worldPartialTicks: Float
            get() = if (Minecraft.getMinecraft().isGamePaused)
                worldTicks + Minecraft.getMinecraft().renderPartialTicksPaused
            else
                worldTicks + Minecraft.getMinecraft().timer.renderPartialTicks

        private var worldTicks = 0
        private var screenTicks = 0

        init { MinecraftForge.EVENT_BUS.register(this) }
        private val animators: MutableSet<Animator> = Collections.newSetFromMap(WeakHashMap<Animator, Boolean>())

        @SubscribeEvent
        fun renderTick(e: TickEvent.RenderTickEvent) {
            animators.forEach { it.update() }
        }

        @SubscribeEvent
        fun tick(e: TickEvent.ClientTickEvent) {
            if(!Minecraft.getMinecraft().isGamePaused) worldTicks++
            screenTicks++
        }
    }
}

package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.client.Minecraft
import net.minecraft.util.Timer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

val Minecraft.renderPartialTicksPaused by MethodHandleHelper.delegateForReadOnly<Minecraft, Float>(Minecraft::class.java, "renderPartialTicksPaused", "field_193996_ah")
val Minecraft.timer by MethodHandleHelper.delegateForReadOnly<Minecraft, Timer>(Minecraft::class.java, "timer", "field_71428_T")

/**
 * An Animator is an object that manages the timing and execution of a number of animations. A single animator is
 * generally used per context. (e.g. one per gui)
 *
 * Animations are added by simply passing them to the [add] method. As of now there is no way to remove animations
 * manually, but that ability is on the roadmap.
 *
 * @sample AnimatorExamples.basic
 */
class Animator {

    init {
        animators.add(this)
    }

    /**
     * If this value is true (which it is by default) this animator will delete any animations that have passed their
     * end time. This keeps old animations from cluttering up memory, keeping references to dead objects, and reduces
     * the amount of processing this animator has to do to sort through its animations.
     *
     * @sample AnimatorExamples.deletePastAnimationsTrue
     * @sample AnimatorExamples.deletePastAnimationsFalse
     */
    var deletePastAnimations = true

    /**
     * If this value is true (which it isn't by default) this animator will pause when the world pauses.
     */
    var useWorldTicks = false

    /**
     * The current time of the animator. By default this is measured in ticks since creation.
     *
     * @sample AnimatorExamples.time
     */
    var time: Float
        get() = partialTicks() * speed - timeOffset
        set(value) {
            timeOffset = partialTicks() * speed - value
        }

    /**
     * The current speed multiplier of the animator
     *
     * @sample AnimatorExamples.speed
     */
    var speed: Float = 1f
        set(value) {
            timeOffset = time + partialTicks() * value
            field = value
        }

    private var addLock = false
        set(value) {
            field = value
            if (!value) {
                this.add(*animationsToAdd.toTypedArray())
                this.animations.removeAll(animationsToRemove)
                animationsToAdd.clear()
                animationsToRemove.clear()
            }
        }

    private fun partialTicks() =
            if (useWorldTicks)
                worldPartialTicks
            else
                screenPartialTicks

    private var timeOffset: Float = partialTicks()

    // sorted in ascending start order so I can quickly cull the expired animations and efficiently queue large numbers
    // of animations without having to iterate over them
    private val animations: MutableSet<Animation<*>> = sortedSetOf(compareBy({ it.start }, { it._id }))
    private val animationsToAdd = mutableListOf<Animation<*>>()
    private val animationsToRemove = mutableListOf<Animation<*>>()
    private val currentAnimations = mutableListOf<Animation<*>>()

    /**
     * Add [animations] to this animator
     */
    fun add(vararg animations: Animation<*>) {
        if (addLock) animationsToAdd.addAll(animations)
        else animations.forEach { animation ->
            if (animation.isInAnimator) {
                throw IllegalArgumentException("Animation already added to animator")
            }
            animation.onAddedToAnimator(this)
            this.animations.add(animation)
        }
    }

    /**
     * Remove all animations involving [obj]
     */
    fun removeAnimationsFor(obj: Any) {
        val inlineRemove = mutableListOf<Animation<*>>()
        animations.forEach {
            if (it.doesInvolveObject(obj)) {
                if (addLock) animationsToRemove.add(it)
                else inlineRemove.add(it)
            }
        }
        if (!addLock) animations.removeAll(inlineRemove)
    }

    /**
     * Removes ALL the animations from this animator
     */
    fun removeAll() {
        if (addLock) animationsToRemove.addAll(animations)
        else animations.clear()
    }

    internal fun update() {
        updateCurrentAnimations()

        currentAnimations.forEach { anim ->
            anim.update(time)
        }
    }

    private fun updateCurrentAnimations() {
        val time = this.time

        currentAnimations.clear()

        performLocked {
            if (deletePastAnimations) animations.removeIf {
                if (it.end < time) {
                    it.update(time)
                    it.complete()
                    true
                } else {
                    false
                }
            }
        }

        currentAnimations.addAll(animations.takeWhile { it.start <= time })
    }

    private inline fun performLocked(block: () -> Unit) {
        addLock = true
        block()
        addLock = false
    }

    internal var nextID: Int = 0

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

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        private val animators: MutableSet<Animator> = Collections.newSetFromMap(WeakHashMap<Animator, Boolean>())

        @SubscribeEvent
        @Suppress("UNUSED_PARAMETER")
        fun renderTick(e: TickEvent.RenderTickEvent) {
            animators.forEach { it.update() }
        }

        @SubscribeEvent
        @Suppress("UNUSED_PARAMETER")
        fun tick(e: TickEvent.ClientTickEvent) {
            if (!Minecraft.getMinecraft().isGamePaused) worldTicks++
            screenTicks++
        }
    }
}

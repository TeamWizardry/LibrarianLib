package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation

/**
 * TODO: Document file zz_Examples
 *
 * Created by TheCodeWarrior
 */
object AnimatorExamples {
    init {
        throw IllegalStateException("WTF are you doing loading the examples class?!")
    }
    val yourObject = Any()
    class YourClass {}
    val animator = Animator()
    val animation = BasicAnimation(Any(), "")

    fun basic() {
        // Create your animator, generally done as a field of your GUI, or somewhere else that allows a single
        // animator per context
        val animator = Animator()

        // create an animation
        val anim = BasicAnimation(yourObject, "yourField")

        // Add your animation to the animator.
        // Some fields of animations become locked when they are added to an animator
        animator.add(anim)
    }

    fun time() {
        // the number of ticks since this animator was created
        val time = animator.time

        // set the time to 0, and continue counting up.
        // If the animator has deletePastAnimations set, none of the old animations will exist anymore, but if that
        // field is unset the old animations will replay from this point
        animator.time = 0f
    }

    fun speed() {
        // The speed multiplier of the animator
        val speed = animator.speed

        // set the speed to -1/2 speed
        // If the animator has deletePastAnimations set, none of the old animations will exist anymore, but if that
        // field is unset the old animations will replay backwards
        animator.speed = -0.5f
    }

    fun deletePastAnimationsTrue() {
        // set to true, the default
        animator.deletePastAnimations = true

        // add an animation that stretches from 0 ticks to 10 ticks
        animator.add(animation)

        /* wait for animation to finish */

        // set the animator time to 0. You will find that the animation you added earlier won't play again. It was
        // deleted the moment it passed
        animator.time = 0f
    }

    fun deletePastAnimationsFalse() {
        // set to false
        animator.deletePastAnimations = false

        // add an animation that stretches from 0 ticks to 10 ticks
        animator.add(animation)

        /* wait for animation to finish */

        // set the animator time to 0. You will find that the animation you added earlier will play again. Because
        // deletePastAnimations was set to false, the animation was kept around, and could be replayed by setting the
        // time backwards a bit
        animator.time = 0f
    }
}

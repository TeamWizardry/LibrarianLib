package com.teamwizardry.librarianlib.test.animator

import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation

/**
 * TODO: Document file zz_Examples
 *
 * Created by TheCodeWarrior
 */
object AnimatorExamples {

    val yourObject = Any()

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
        // set the time to 0, and continue counting up.
        // If the animator has deletePastAnimations set, none of the old animations will exist anymore, but if that
        // field is unset the old animations will replay from this point
        animator.time = 0f
    }

    fun speed() {
        // set the speed to -1/2 speed
        // If the animator has deletePastAnimations set, none of the old animations will exist anymore, but if that
        // field is unset the old animations will replay backwards
        animator.speed = -0.5f
    }
}

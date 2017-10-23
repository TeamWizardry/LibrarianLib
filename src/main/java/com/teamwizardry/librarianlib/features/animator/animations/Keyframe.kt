package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.Easing

/**
 * A keyframe in a [KeyframeAnimation]
 *
 * @property time The time of the keyframe. This should be between 0 and 1, 0 being the beginning of the animation,
 *              1 being the end.
 * @property value The value at the time of the keyframe
 * @property easing The easing to use when transitioning between the previous keyframe and this keyframe
 */
data class Keyframe(val time: Float, val value: Any, val easing: Easing = Easing.linear)

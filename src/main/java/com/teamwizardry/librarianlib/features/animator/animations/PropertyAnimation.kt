package com.teamwizardry.librarianlib.features.animator.animations

import com.teamwizardry.librarianlib.features.animator.AnimatableProperty
import com.teamwizardry.librarianlib.features.animator.Animation

abstract class PropertyAnimation<T : Any>(target: T, val property: AnimatableProperty<T>): Animation<T>(target)
package com.teamwizardry.librarianlib.facade.container.layers

import com.teamwizardry.librarianlib.math.Vec2d

/**
 * An interface for layers that want to expose an ingredient to JEI. This is used when pressing the key to view
 * something's recipe or its uses. The returned object must be an ingredient type registered with JEI.
 */
public interface JeiIngredientLayer {
    /**
     * The ingredient the the given local position
     */
    public fun getJeiIngredient(pos: Vec2d): Any?
}
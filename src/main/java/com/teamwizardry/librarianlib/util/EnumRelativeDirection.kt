package com.teamwizardry.librarianlib.util

import net.minecraft.util.IStringSerializable

/**
 * Created by Saad on 6/14/2016.
 */
enum class EnumRelativeDirection private constructor(var direction: String?) : IStringSerializable {
    LEFT("left"), RIGHT("right");

    override fun getName(): String? {
        return null
    }
}

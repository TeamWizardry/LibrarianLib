package com.teamwizardry.librarianlib.features.base.block

import net.minecraft.util.IStringSerializable
import java.util.*

/**
 * @author WireSegal
 * Created at 1:22 PM on 1/8/17.
 */
interface EnumStringSerializable : IStringSerializable {
    override fun getName() = (this as Enum<*>).name.toLowerCase(Locale.ROOT)
}

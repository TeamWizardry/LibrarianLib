package com.teamwizardry.librarianlib.features.base.block.tile

import net.minecraft.util.ITickable

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileModTickable : TileMod(), ITickable {
    abstract fun tick()

    override final fun update() {
        modules.forEach { it.value.onUpdate(this) }
        tick()
    }
}

package com.teamwizardry.librarianlib.features.base.block.tile

import net.minecraft.util.ITickable

/**
 * Created by TheCodeWarrior
 */
abstract class TileModInventoryTickable(size: Int) : TileModInventory(size), ITickable {
    abstract fun tick()

    override final fun update() {
        modules.forEach { it.value.onUpdate(this) }
        tick()
    }
}

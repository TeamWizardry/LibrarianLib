package com.teamwizardry.librarianlib.features.base.block.tile

/**
 * Created by TheCodeWarrior
 */
abstract class TileModInventoryTickable(size: Int) : TileModInventory(size), net.minecraft.util.ITickable {
    abstract fun tick()

    override final fun update() {
        modules.forEach { it.value.onUpdate(this) }
        tick()
    }
}

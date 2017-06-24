package com.teamwizardry.librarianlib.features.base.block.tile

/**
 * Created by TheCodeWarrior
 */
open class TileModInventory(@com.teamwizardry.librarianlib.features.saving.Module val module: com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory) : TileMod(), net.minecraftforge.items.IItemHandlerModifiable by module.handler {
    constructor(size: Int) : this(com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory(size))
}

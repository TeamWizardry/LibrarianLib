package com.teamwizardry.librarianlib.features.base.block.tile

import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by TheCodeWarrior
 */
open class TileModInventory(@Module val module: ModuleInventory) : TileMod(), IItemHandlerModifiable by module.handler {
    constructor(size: Int) : this(ModuleInventory(size))
}

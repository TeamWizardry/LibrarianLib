package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.features.base.block.module.ModuleInventory
import com.teamwizardry.librarianlib.features.saving.Module
import net.minecraftforge.items.IItemHandlerModifiable

/**
 * Created by TheCodeWarrior
 */
open class TileModInventory protected constructor(@Module val module: ModuleInventory) : TileMod(), IItemHandlerModifiable by module.handler {
    constructor(size: Int) : this(ModuleInventory(size))
}

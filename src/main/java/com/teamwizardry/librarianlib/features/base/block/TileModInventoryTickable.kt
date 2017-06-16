package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.features.kotlin.isNotEmpty
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter
import net.minecraft.item.ItemStack
import net.minecraft.util.ITickable
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemHandlerHelper
import java.util.*

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

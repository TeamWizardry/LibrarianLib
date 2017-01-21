package com.teamwizardry.librarianlib.test.items

import com.teamwizardry.librarianlib.common.base.Ignored
import com.teamwizardry.librarianlib.common.base.ResourceClass
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import com.teamwizardry.librarianlib.common.util.toStack
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by Elad on 1/21/2017.
 */
@ResourceClass
object ModItems {
    lateinit var foo: ItemFoo

    @Ignored
    var bar: ItemBar
    fun test(player: EntityPlayer) {
        player.inventory.addItemStackToInventory(foo.toStack())
        player.inventory.addItemStackToInventory(bar.toStack())
    }

    init {
        bar = ItemBar()
    }
}

class ItemFoo : ItemMod("foo")
class ItemBar : ItemMod("bar")
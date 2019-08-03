package com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.criterion.game

import com.teamwizardry.librarianlib.gui.provided.book.hierarchy.entry.Entry
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * @author WireSegal
 * Created at 10:02 PM on 2/21/18.
 */
class EntryUnlockedEvent(val player: EntityPlayer, val entry: Entry, var result: Boolean = false) : Event()

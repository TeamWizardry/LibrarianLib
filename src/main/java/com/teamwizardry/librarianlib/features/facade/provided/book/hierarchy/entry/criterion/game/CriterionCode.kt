package com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.game

import com.teamwizardry.librarianlib.features.facade.provided.book.hierarchy.entry.criterion.ICriterion
import net.minecraft.entity.player.EntityPlayer

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
class CriterionCode : ICriterion {

    override fun isUnlocked(player: EntityPlayer, grantedInCode: Boolean) = grantedInCode
}

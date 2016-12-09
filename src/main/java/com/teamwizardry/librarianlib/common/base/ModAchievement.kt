package com.teamwizardry.librarianlib.common.base

import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.stats.Achievement
import net.minecraftforge.common.AchievementPage
import net.minecraftforge.fml.common.Loader

/**
 * @author WireSegal
 * Created at 4:22 PM on 12/8/16.
 */
@Suppress("LeakingThis")
open class ModAchievement(unlocalizedName: String, column: Int, row: Int, stack: ItemStack, parent: Achievement?) : Achievement("achievement.$currentModId.$unlocalizedName", "$currentModId.$unlocalizedName", column, row, stack, parent) {

    init {
        registerStat()
        achievements.getOrPut(currentModId) { mutableListOf() }.add(this)
    }

    constructor(unlocalizedName: String, column: Int, row: Int, blockIn: Block, parent: Achievement?) : this(unlocalizedName, column, row, ItemStack(blockIn), parent)
    constructor(unlocalizedName: String, column: Int, row: Int, itemIn: Item, parent: Achievement?) : this(unlocalizedName, column, row, ItemStack(itemIn), parent)

    companion object {
        private val achievements = mutableMapOf<String, MutableList<ModAchievement>>()

        @JvmStatic
        fun producePage(): AchievementPage {
            val achievements = achievements.getOrElse(currentModId) { return AchievementPage.getAchievementPage(0) }
            val name = Loader.instance().indexedModList[currentModId]?.name ?: return AchievementPage.getAchievementPage(0)
            val page = AchievementPage(name, *achievements.toTypedArray())
            AchievementPage.registerAchievementPage(page)
            return page
        }
    }
}

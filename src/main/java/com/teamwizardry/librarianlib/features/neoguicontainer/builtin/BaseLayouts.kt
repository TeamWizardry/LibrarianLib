package com.teamwizardry.librarianlib.features.neoguicontainer.builtin

import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.features.container.internal.SlotBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.neoguicontainer.ComponentSlot

/**
 * Created by TheCodeWarrior
 */
object BaseLayouts {

    fun player(inv: BaseWrappers.InventoryWrapperPlayer) = PlayerLayout(inv)

    fun grid(inv: InventoryWrapper, rowLength: Int) = GridLayout(inv.slotArray, rowLength)

    fun grid(inv: List<SlotBase>, rowLength: Int) = GridLayout(inv, rowLength)

    class GridLayout(inv: List<SlotBase>, rowLength: Int) {
        val rowCount = (inv.size + rowLength - 1) / rowLength
        val root = GuiComponent(0, 0)
        val rows = Array(rowCount) {
            val row = GuiComponent(0, it * 18)
            root.add(row)
            row
        }
        val slots = Array(rowCount) { row ->
            Array(if (row == rowCount - 1) inv.size - rowLength * row else rowLength) { column ->
                val index = row * rowLength + column

                val slot = ComponentSlot(inv[index], column * 18, 0)
                rows[row].add(slot)

                slot
            }
        }
    }

    class PlayerLayout(player: BaseWrappers.InventoryWrapperPlayer) {
        val root: GuiComponent
        val armor: GuiComponent
        val mainWrapper: GuiComponent
        val main: GuiComponent
        val hotbar: GuiComponent
        val offhand: GuiComponent

        init {
            armor = GuiComponent(0, 0)
            armor.isVisible = false
            armor.add(
                    ComponentSlot(player.head, 0, 0),
                    ComponentSlot(player.chest, 0, 18),
                    ComponentSlot(player.legs, 0, 2 * 18),
                    ComponentSlot(player.feet, 0, 3 * 18)
            )

            offhand = GuiComponent(0, 0)
            offhand.isVisible = false
            offhand.add(
                    ComponentSlot(player.offhand, 0, 0)
            )

            mainWrapper = GuiComponent(0, 0)

            main = GuiComponent(0, 0)
            for (row in 0..2) {
                for (column in 0..8) {
                    main.add(ComponentSlot(player.main[row * 9 + column], column * 18, row * 18))
                }
            }

            hotbar = GuiComponent(0, 58)
            for (column in 0..8) {
                hotbar.add(ComponentSlot(player.hotbar[column], column * 18, 0))
            }
            main.add(hotbar)
            mainWrapper.add(main)

            root = GuiComponent(0, 0)
            root.add(armor, mainWrapper, offhand)
        }
    }
}

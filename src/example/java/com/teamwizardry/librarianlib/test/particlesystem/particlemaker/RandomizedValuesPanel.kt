package com.teamwizardry.librarianlib.test.particlesystem.particlemaker

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.neogui.layers.TextLayer
import com.teamwizardry.librarianlib.features.neogui.layout.Flexbox
import com.teamwizardry.librarianlib.features.neogui.layout.flex
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastrySwitch
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.components.PastryToggle
import com.teamwizardry.librarianlib.features.neogui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.neogui.value.GuiAnimator
import com.teamwizardry.librarianlib.features.neogui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import kotlin.math.max

class RandomizedValuesPanel(val onEdit: (RandomizedValuesPanel) -> Unit) : PastryWindow(200, 100, PastryWindow.Style.PANEL, false) {
    private val flexbox = Flexbox(0, 0, 200, 100)
    private val columns: List<ComponentColumn>
    private val label = TextLayer(16, 2, 30, 11)

    val randomizedSwitch = PastrySwitch(2, 2)

    val columnItemX = ComponentColumn("X")
    val columnItemY = ComponentColumn("Y")
    val columnItemZ = ComponentColumn("Z")

    init {
        title = "Velocity"

        label.text = "Randomize"
        label.wrap = false

        flexbox.alignItems = Flexbox.Align.START
        flexbox.stretch = false

        content.add(randomizedSwitch, label, flexbox)

        columns = listOf(columnItemX, columnItemY, columnItemZ)

        columns.forEach {
            it.flex.minSize = it.widthi
        }
        flexbox.add(*columns.toTypedArray())

        BUS.hook<GuiWindow.LoseFocusEvent> {
            this.close()
        }
        randomizedSwitch.BUS.hook<PastryToggle.StateChangeEvent> { event ->
            this.add(GuiAnimator.animate(8f) {
                modeSwitched(!event.newState)
            })
        }

        runLayoutIfNeeded()
        modeSwitched(true)
    }

    private fun modeSwitched(fixed: Boolean) {
        columns.forEach {
            it.fixed = fixed
        }
        contentSize = vec(contentSize.x, flexbox.pos.y + flexbox.minSize.y + 2)
        runLayoutIfNeeded()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        flexbox.pos = vec(2, 4 + label.height)
        flexbox.size = vec(this.width - 4, flexbox.minSize.y)
    }

    sealed class RandomizedComponent {
        data class Fixed(val value: Double) : RandomizedComponent()
        data class Range(val min: Double, val max: Double) : RandomizedComponent()
    }

    inner class ComponentColumn(val columnName: String) : GuiComponent(50, 20) {
        var fixed = true
            set(value) {
                field = value
                minItem.showPrefix = !fixed
                this.height = if (fixed) 10.0 else 20.0
            }
        val minItem = Item("Min ")
        val maxItem = Item("Max ")

        val minWidth = max(
                minItem.flexbox.minSize.x,
                maxItem.flexbox.minSize.x
        )

        init {
            this.width = minWidth
            this.heighti = 10

            clipToBounds = true
            maxItem.y = minItem.height
            minItem.showPrefix = false
            add(minItem, maxItem)

            val maxLabelWidth = max(minItem.labelWidth, maxItem.labelWidth)
            minItem.labelWidth = maxLabelWidth
            minItem.label.widthi = maxLabelWidth
            maxItem.labelWidth = maxLabelWidth
            maxItem.label.widthi = maxLabelWidth
        }

        override fun layoutChildren() {
            super.layoutChildren()
            minItem.width = this.width
            maxItem.width = this.width
        }

        inner class Item(prefix: String) : GuiComponent(0, 0, 50, 10) {
            val flexbox = Flexbox(0, 0, 50, 10, Cardinal2d.GUI.LEFT)

            var showPrefix = true
                set(value) {
                    field = value
                    label.widthi = if (showPrefix) labelWidth else labelWidthNoPrefix
                }

            val field = ComponentTextField(0, 0, 30, 16)
            val label = TextLayer(0, 0, 20, 16)

            var labelWidth = Minecraft().fontRenderer.getStringWidth(prefix + columnName)
            var labelWidthNoPrefix = Minecraft().fontRenderer.getStringWidth(columnName)

            init {
                flexbox.justifyContent = Flexbox.Justify.CENTER
                flexbox.spacing = 2

                label.text = prefix + columnName
                label.wrap = false
                label.align = Align2d.TOP_RIGHT
                label.widthi = labelWidth
                label.clipToBounds = true

                field.flex.config(
                        flexGrow = 0, flexShrink = 0
                )
                label.componentWrapper().flex.config(
                        flexGrow = 0, flexShrink = 0
                )

                this.add(flexbox)
                flexbox.add(field, label.componentWrapper())

                //  field.writeText("0.0")
                field.BUS.hook<ComponentTextField.PostTextEditEvent> { event ->
                    try {
                        event.whole.toDouble()
                        onEdit(this@RandomizedValuesPanel)
                    } catch (ignored: NumberFormatException) {
                    }
                }
            }

            override fun layoutChildren() {
                super.layoutChildren()
                flexbox.frame = this.bounds
            }
        }
    }

    fun setInputOf(item: ComponentColumn.Item, d: Double) {
        item.field.writeText("$d")
    }

    fun getInputOf(item: ComponentColumn.Item): Double {
        return try {
            item.field.text.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
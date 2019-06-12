package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.layers.minecraft.FluidGaugeLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.GuiPastryBase
import com.teamwizardry.librarianlib.features.facade.provided.pastry.Pastry
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.PastryLabel
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.DropdownTextItem
import com.teamwizardry.librarianlib.features.facade.provided.pastry.components.dropdown.PastryDropdown
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import net.minecraftforge.fluids.FluidRegistry

@UseExperimental(ExperimentalBitfont::class)
class GuiTestFluidGauge: GuiPastryBase() {

    val directionDropdown: PastryDropdown<Cardinal2d>
    val flowDropdown: PastryDropdown<Cardinal2d?>
    val fluidGauge = FluidGaugeLayer(0, 0)
    val fluidGaugeBg = PastryBackground(BackgroundTexture.SLIGHT_INSET, 0, 0, 100, 50)

    init {
        main.size = vec(200, 200)

        // ======= direction
        directionDropdown = PastryDropdown(3, 0, 0) {
            fluidGauge.direction = it
            when(it.axis) {
                Axis2d.X -> fluidGaugeBg.size = vec(100, 50)
                Axis2d.Y -> fluidGaugeBg.size = vec(50, 100)
            }
        }
        directionDropdown.items.addAll(Cardinal2d.values().map {
            DropdownTextItem(it, it.toString())
        })
        directionDropdown.sizeToFit()
        directionDropdown.select(Cardinal2d.UP)

        // ======= flow
        flowDropdown = PastryDropdown(3, 0, 0) {
            fluidGauge.flow = it
        }
        flowDropdown.items.addAll(listOf(null, *Cardinal2d.values()).map {
            DropdownTextItem(it, it?.toString() ?: "None")
        })
        flowDropdown.sizeToFit()
        flowDropdown.select(null)

        // ======= guage
        fluidGaugeBg.add(fluidGauge)
        fluidGaugeBg.BUS.hook<GuiLayerEvents.LayoutChildren> {
            fluidGauge.frame = fluidGaugeBg.bounds.shrink(1.0)
        }

        fluidGauge.fluid = FluidRegistry.LAVA
        fluidGauge.fillFraction_im.animate(0.0, 1.0, 200f, Easing.easeInOutSine).also {
            it.repeatCount = -1
            it.shouldReverse = true
        }

        // ======= main layout
        main.add(StackLayout.build()
            .vertical()
            .size(main.size).space(5)
            .alignTop().alignCenterX()

            .add(GuiComponent(0, 0, 0, Pastry.lineHeight).also { row ->
                row.add(
                    PastryLabel(0, 2, "Direction").also { it.x = -it.width - 3 },
                    directionDropdown
                )
            })
            .add(GuiComponent(0, 0, 0, Pastry.lineHeight).also { row ->
                row.add(
                    PastryLabel(0, 2, "Flow").also { it.x = -it.width - 3 },
                    flowDropdown
                )
            })
            .add(GuiLayer(0, 0, 0, 10)) // spacer
            .add(fluidGaugeBg)

            .component()
        )
    }
}

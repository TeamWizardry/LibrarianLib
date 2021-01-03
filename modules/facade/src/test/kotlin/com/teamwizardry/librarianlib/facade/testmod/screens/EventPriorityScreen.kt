package com.teamwizardry.librarianlib.facade.testmod.screens

import com.teamwizardry.librarianlib.core.util.loc
import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent
import com.teamwizardry.librarianlib.etcetera.eventbus.EventBus
import com.teamwizardry.librarianlib.facade.FacadeScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.testmod.LibrarianLibFacadeTestMod
import com.teamwizardry.librarianlib.mosaic.Mosaic
import net.minecraft.util.text.ITextComponent
import java.awt.Color

class EventPriorityScreen(title: ITextComponent): FacadeScreen(title) {
    init {
        val dirt = Mosaic(loc("minecraft:textures/block/dirt.png"), 16, 16)

        val bus = EventBus()
        val fireOrder = mutableListOf<Int>()

        fun hook(order: Int, priority: EventBus.Priority) {
            bus.hook<TestEvent>(priority) {
                fireOrder.add(order)
                logger.info("$order $priority")
            }
        }

        hook(0, EventBus.Priority.FIRST)
        hook(2, EventBus.Priority.EARLY)
        hook(4, EventBus.Priority.DEFAULT)
        hook(6, EventBus.Priority.LATE)
        hook(8, EventBus.Priority.LAST)

        hook(9, EventBus.Priority.LAST)
        hook(7, EventBus.Priority.LATE)
        hook(5, EventBus.Priority.DEFAULT)
        hook(3, EventBus.Priority.EARLY)
        hook(1, EventBus.Priority.FIRST)

        val cancelOrder = mutableListOf<Int>()

        bus.hook<TestCancelEvent> {
            logger.info("Canceling event")
            cancelOrder.add(0)
            it.cancel()
        }
        bus.hook<TestCancelEvent> {
            logger.error("Normal hook received canceled event!")
            cancelOrder.add(-1) // shouldn't fire
        }
        bus.hook<TestCancelEvent>(EventBus.Priority.DEFAULT, true) {
            logger.info("Received canceled event")
            cancelOrder.add(1) // should fire
        }

        bus.fire(TestEvent())
        bus.fire(TestCancelEvent())

        val success = fireOrder == List(10) { it } && cancelOrder == listOf(0, 1)

        val layer = RectLayer(if(success) Color.GREEN else Color.RED, 0, 0, 16, 16)

        facade.main.add(layer)
        facade.main.size = layer.size
    }

    class TestEvent: CancelableEvent()
    class TestCancelEvent: CancelableEvent()

    private companion object {
        val logger = LibrarianLibFacadeTestMod.makeLogger<EventPriorityScreen>()
    }
}
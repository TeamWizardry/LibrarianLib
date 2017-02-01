package com.teamwizardry.librarianlib.client.gui

import com.teamwizardry.librarianlib.client.core.ClientTickHandler
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.client.util.F3Handler
import com.teamwizardry.librarianlib.common.util.div
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Created by TheCodeWarrior
 */
object GuiOverlay {

    private data class StorageThing(val initializer: Consumer<GuiComponent<*>>, val visible: BooleanSupplier) {
        fun reinit(main: ComponentVoid) {
            val comp = ComponentVisiblePredicate(visible)
            main.add(comp)
            initializer.accept(comp)
        }
    }

    private var mainComp = ComponentVoid(0,0)
    private val registered = mutableSetOf<StorageThing>()
    private val newlyRegistered = mutableSetOf<StorageThing>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        F3Handler.addHandler(Keyboard.KEY_O, "Reinitialize overlay components", Supplier { "Reloaded overlays" }, Runnable {
            mainComp = ComponentVoid(0, 0)
            registered.forEach {
                it.reinit(mainComp)
            }
        })
    }

    /**
     * Get an overlay component and show it when [visible] returns true.
     *
     * Hook into [GuiComponent.ComponentTickEvent] to update its position and/or size
     */
    fun getOverlayComponent(visible: BooleanSupplier, initializer: Consumer<GuiComponent<*>>) {
        val storage = StorageThing(initializer, visible)
        storage.reinit(mainComp)
        registered.add(storage)
        newlyRegistered.add(storage)
    }

    @SubscribeEvent
    fun overlay(e: RenderGameOverlayEvent.Post) {
        if(e.type != RenderGameOverlayEvent.ElementType.ALL) return
        val res = ScaledResolution(Minecraft.getMinecraft())
        GlStateManager.enableBlend()
        mainComp.size = vec(res.scaledWidth, res.scaledHeight)
        mainComp.draw(mainComp.size/2, ClientTickHandler.partialTicks)
    }

    @SubscribeEvent
    fun tick(e: TickEvent.ClientTickEvent) {
        newlyRegistered.forEach {
            it.reinit(mainComp)
        }
        newlyRegistered.clear()
        mainComp.tick()
    }

    private class ComponentVisiblePredicate(val predicate: BooleanSupplier) : GuiComponent<ComponentVisiblePredicate>(0,0) {
        override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
            // noop
        }

        override fun onTick() {
            this.isVisible = predicate.asBoolean
        }
    }
}

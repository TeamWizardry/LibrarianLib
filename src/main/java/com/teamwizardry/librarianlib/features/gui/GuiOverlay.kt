package com.teamwizardry.librarianlib.features.gui

import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.utilities.client.F3Handler
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Created by TheCodeWarrior
 */
object GuiOverlay {

    private data class StorageThing(val initializer: Consumer<GuiComponent>, val visible: BooleanSupplier) {
        fun reinit(main: GuiComponent) {
            val comp = ComponentVisiblePredicate(visible)
            main.add(comp)
            initializer.accept(comp)
        }
    }

    private var mainComp = GuiComponent(0, 0)
    private val registered = mutableSetOf<StorageThing>()
    private val newlyRegistered = mutableSetOf<StorageThing>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        F3Handler.addHandler(Keyboard.KEY_O, "Reinitialize overlay components", Supplier { "Reloaded overlays" }, Runnable {
            mainComp = GuiComponent(0, 0)
            registered.forEach {
                it.reinit(mainComp)
            }
        })
    }

    /**
     * Get an overlay component and show it when [visible] returns true.
     *
     * Hook into [GuiComponentEvents.ComponentTickEvent] to update its position and/or size
     */
    fun getOverlayComponent(visible: BooleanSupplier, initializer: Consumer<GuiComponent>) {
        val storage = StorageThing(initializer, visible)
        storage.reinit(mainComp)
        registered.add(storage)
        newlyRegistered.add(storage)
    }

    @SubscribeEvent
    fun overlay(e: RenderGameOverlayEvent.Post) {
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return
        val res = ScaledResolution(Minecraft.getMinecraft())
        GlStateManager.enableBlend()
        mainComp.size = vec(res.scaledWidth, res.scaledHeight)

        StencilUtil.clear()
        GL11.glEnable(GL11.GL_STENCIL_TEST)
//        mainComp.renderRoot(ClientTickHandler.partialTicks)
        GL11.glDisable(GL11.GL_STENCIL_TEST)
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun tick(e: TickEvent.ClientTickEvent) {
        newlyRegistered.forEach {
            it.reinit(mainComp)
        }
        newlyRegistered.clear()
        mainComp.tick()
    }

    private class ComponentVisiblePredicate(val predicate: BooleanSupplier) : GuiComponent(0, 0) {
        override fun draw(partialTicks: Float) {
            // NO-OP
        }

        @Hook
        @Suppress("UNUSED_PARAMETER")
        fun onTick(e: GuiComponentEvents.ComponentTickEvent) {
            this.isVisible = predicate.asBoolean
        }
    }
}

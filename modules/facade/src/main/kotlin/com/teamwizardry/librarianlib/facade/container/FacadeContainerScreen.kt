package com.teamwizardry.librarianlib.facade.container

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.facade.FacadeMouseMask
import com.teamwizardry.librarianlib.facade.FacadeWidget
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.bridge.FacadeContainerScreenHooks
import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import com.teamwizardry.librarianlib.facade.container.messaging.MessageEncoder
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.ContainerSpace
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryDynamicBackground
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import org.lwjgl.opengl.GL11

/**
 * A Facade-backed GUI container.
 */
public abstract class FacadeContainerScreen<T: Container>(
    container: T,
    inventory: PlayerInventory,
    title: ITextComponent
): ContainerScreen<T>(container, inventory, title), FacadeMouseMask, FacadeContainerScreenHooks {
    public val player: PlayerEntity = inventory.player

    @Suppress("LeakingThis")
    public val facade: FacadeWidget = FacadeWidget(this)

    /**
     * The main root layer. This layer appears below the container's items and controls the GUI size. To draw a layer
     * above the items, add a layer to [facade].[root][FacadeWidget.root] with a z-index of 1000 or greater.
     */
    public val main: GuiLayer = facade.main

    /**
     * A fullscreen layer that appears above items
     */
    public val rootOverlay: GuiLayer = GuiLayer()

    /**
     * An equivalent to [main] that appears above items
     */
    public val mainOverlay: GuiLayer = GuiLayer()

    public val background: PastryDynamicBackground = PastryDynamicBackground(PastryBackgroundStyle.VANILLA, main)

    public val jei: JeiIntegration = JeiIntegration()

    private val messageEncoder = MessageEncoder(container.javaClass, container.windowId)

    init {
        background.zIndex = GuiLayer.BACKGROUND_Z
        main.add(background)

        rootOverlay.ignoreMouseOverBounds = true
        rootOverlay.zIndex = 1000.0
        mainOverlay.ignoreMouseOverBounds = true

        facade.root.hook<GuiLayerEvents.LayoutChildren> {
            rootOverlay.frame = facade.root.frame
            mainOverlay.frame = main.frame
        }
        rootOverlay.add(mainOverlay)
        facade.root.add(rootOverlay)
    }

    /**
     * Sends a message to both the client and server containers. Most actions should be performed using this method,
     * which will lead to easier synchronization between the client and server containers.
     */
    public fun sendMessage(name: String, vararg arguments: Any?) {
        LibrarianLibFacadeModule.channel.sendToServer(messageEncoder.encode(name, arguments))
        messageEncoder.invoke(container, name, arguments)
    }

    override fun render(matrixStack: MatrixStack, p_render_1_: Int, p_render_2_: Int, p_render_3_: Float) {
        facade.update()
        var frame = main.frame
        if(background.isVisible) {
            frame = frame.grow(background.style.edgeSize - background.style.edgeInset)
        }
        xSize = frame.widthi
        ySize = frame.heighti
        guiLeft = frame.xi
        guiTop = frame.yi
        ContainerSpace.guiLeft = guiLeft
        ContainerSpace.guiTop = guiTop

        this.renderBackground(matrixStack)
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_)
        if(!facade.hasTooltip)
            this.renderHoveredTooltip(matrixStack, p_render_1_, p_render_2_)
    }

    override fun isMouseMasked(mouseX: Double, mouseY: Double): Boolean {
        return getEventListenerForPos(mouseX, mouseY).isPresent || container.inventorySlots.any {
            isPointInRegion(it.xPos, it.yPos, 16, 16, mouseX, mouseY) && it.isEnabled
        }
    }

    override fun isSlotSelectedHook(slotIn: Slot?, mouseX: Double, mouseY: Double, result: Boolean): Boolean {
        return result && !facade.hitTest(mouseX, mouseY).isOverVanilla
    }

    override fun hasClickedOutside(mouseX: Double, mouseY: Double, guiLeftIn: Int, guiTopIn: Int, mouseButton: Int): Boolean {
        return facade.hitTest(mouseX, mouseY).layer == null
    }

    override fun drawGuiContainerBackgroundLayer(matrixStack: MatrixStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        facade.filterRendering { it.zIndex < 1000 }
        facade.render(matrixStack)
    }

    override fun drawGuiContainerForegroundLayer(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        RenderSystem.enableDepthTest() // depth testing is disabled because... logic?

        RenderSystem.translatef(-guiLeft.toFloat(), -guiTop.toFloat(), 0.0f)

        RenderSystem.colorMask(false, false, false, false)
        RenderSystem.depthFunc(GL11.GL_ALWAYS)
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(depthClobberRenderType)

        val windowHeight = Client.window.scaledHeight
        val windowWidth = Client.window.scaledWidth
        vb.pos2d(Matrix4d.IDENTITY, 0, windowHeight).color(1f, 0f, 1f, 0.5f).endVertex()
        vb.pos2d(Matrix4d.IDENTITY, windowWidth, windowHeight).color(1f, 0f, 1f, 0.5f).endVertex()
        vb.pos2d(Matrix4d.IDENTITY, windowWidth, 0).color(1f, 0f, 1f, 0.5f).endVertex()
        vb.pos2d(Matrix4d.IDENTITY, 0, 0).color(1f, 0f, 1f, 0.5f).endVertex()

        buffer.finish()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)
        RenderSystem.colorMask(true, true, true, true)

        facade.filterRendering { it.zIndex >= 1000 }
        facade.render(matrixStack)
        RenderSystem.translatef(guiLeft.toFloat(), guiTop.toFloat(), 0.0f)

        RenderSystem.disableDepthTest()
    }

    override fun mouseMoved(xPos: Double, mouseY: Double) {
        facade.mouseMoved(xPos, mouseY)
        super.mouseMoved(xPos, mouseY)
    }

    override fun charTyped(p_charTyped_1_: Char, p_charTyped_2_: Int): Boolean {
        if(super.charTyped(p_charTyped_1_, p_charTyped_2_))
            return true
        return facade.charTyped(p_charTyped_1_, p_charTyped_2_)
    }

//    override fun func_212932_b(eventListener: IGuiEventListener?) {
//        facade.func_212932_b(eventListener)
//    }

//    override fun getEventListenerForPos(mouseX: Double, mouseY: Double): Optional<IGuiEventListener> {
//        return facade.getEventListenerForPos(mouseX, mouseY)
//    }

    override fun keyPressed(p_keyPressed_1_: Int, p_keyPressed_2_: Int, p_keyPressed_3_: Int): Boolean {
        if(super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_))
            return true
        return facade.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if(super.keyReleased(keyCode, scanCode, modifiers))
            return true
        return facade.keyReleased(keyCode, scanCode, modifiers)
    }

    private val blockedDowns = mutableSetOf<Int>()

    override fun mouseClicked(p_mouseClicked_1_: Double, p_mouseClicked_3_: Double, p_mouseClicked_5_: Int): Boolean {
        facade.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
        if(facade.mouseHit.isOverVanilla)
            blockedDowns.add(p_mouseClicked_5_)
        else
            super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)
        return true
    }

    override fun mouseReleased(p_mouseReleased_1_: Double, p_mouseReleased_3_: Double, p_mouseReleased_5_: Int): Boolean {
        facade.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)
        if(p_mouseReleased_5_ !in blockedDowns)
            super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)
        blockedDowns.remove(p_mouseReleased_5_)
        return true
    }

    override fun mouseScrolled(p_mouseScrolled_1_: Double, p_mouseScrolled_3_: Double, p_mouseScrolled_5_: Double): Boolean {
        facade.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_)
        if(!facade.mouseHit.isOverVanilla)
            super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_)
        return true
    }

//    override fun setFocusedDefault(eventListener: IGuiEventListener?) {
//        facade.setFocusedDefault(eventListener)
//    }

    override fun mouseDragged(p_mouseDragged_1_: Double, p_mouseDragged_3_: Double, p_mouseDragged_5_: Int, p_mouseDragged_6_: Double, p_mouseDragged_8_: Double): Boolean {
        facade.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)
        if(!facade.mouseHit.isOverVanilla)
            super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)
        return true
    }

    override fun changeFocus(p_changeFocus_1_: Boolean): Boolean {
        facade.changeFocus(p_changeFocus_1_)
        // todo: implement facade focus system and integrate
        super.changeFocus(p_changeFocus_1_)
        return true
    }

    override fun onClose() {
        super.onClose()
        facade.onClose()
    }

    public inner class JeiIntegration {
        private val _exclusionAreas: MutableList<GuiLayer> = mutableListOf()
        public val exclusionAreas: List<GuiLayer> = _exclusionAreas.unmodifiableView()

        public fun addExclusionArea(layer: GuiLayer) {
            _exclusionAreas.add(layer)
        }

        public fun removeExclusionArea(layer: GuiLayer) {
            _exclusionAreas.remove(layer)
        }

        @JvmSynthetic
        internal fun acceptJeiGhostStack(slot: GhostSlot, stack: ItemStack) {
            sendMessage("acceptJeiGhostStack", slot.slotNumber, stack)
        }
    }

    public companion object {
        private val depthClobberRenderType = SimpleRenderTypes.flat(GL11.GL_QUADS) {
            it.depthTest(DefaultRenderStates.DEPTH_ALWAYS)
        }
    }
}
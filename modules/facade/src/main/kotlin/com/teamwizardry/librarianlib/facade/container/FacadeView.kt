package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.state.RenderState
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.facade.FacadeMouseMask
import com.teamwizardry.librarianlib.facade.FacadeWidget
import com.teamwizardry.librarianlib.facade.bridge.FacadeContainerScreenHooks
import com.teamwizardry.librarianlib.facade.container.messaging.MessageEncoder
import com.teamwizardry.librarianlib.facade.container.messaging.MessageSender
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layer.supporting.ContainerSpace
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle
import com.teamwizardry.librarianlib.facade.pastry.layers.PastryDynamicBackground
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text

/**
 * A Facade-backed GUI container.
 */
public abstract class FacadeView<T: ScreenHandler>(
    container: T,
    inventory: PlayerInventory,
    title: Text
): HandledScreen<T>(container, inventory, title), FacadeMouseMask, FacadeContainerScreenHooks {
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

    private val messageEncoder = MessageEncoder(container.javaClass, container.syncId)

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
        MessageSender.ClientToServer.send(messageEncoder.encode(name, arguments))
        messageEncoder.invoke(this.handler, name, arguments)
    }

    override fun render(matrixStack: MatrixStack, p_render_1_: Int, p_render_2_: Int, p_render_3_: Float) {
        facade.update()
        var frame = main.frame
        if(background.isVisible) {
            frame = frame.grow(background.style.edgeSize - background.style.edgeInset)
        }
        backgroundWidth = frame.widthi
        backgroundHeight = frame.heighti
        x = frame.xi
        y = frame.yi
        ContainerSpace.guiLeft = x
        ContainerSpace.guiTop = y

        this.renderBackground(matrixStack)
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_)
        if(!facade.hasTooltip)
            this.drawMouseoverTooltip(matrixStack, p_render_1_, p_render_2_)
    }

    override fun isMouseMasked(mouseX: Double, mouseY: Double): Boolean {
        return hoveredElement(mouseX, mouseY).isPresent || handler.slots.any {
            isPointWithinBounds(it.x, it.y, 16, 16, mouseX, mouseY) && it.isEnabled
        }
    }

    override fun isSlotSelectedHook(slotIn: Slot?, mouseX: Double, mouseY: Double, result: Boolean): Boolean {
        return result && !facade.hitTest(mouseX, mouseY).isOverVanilla
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, guiLeftIn: Int, guiTopIn: Int, mouseButton: Int): Boolean {
        return !isMouseMasked(mouseX, mouseY) && facade.hitTest(mouseX, mouseY).layer == null
    }

    override fun drawBackground(matrixStack: MatrixStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        facade.filterRendering { it.zIndex < 1000 }
        facade.render(matrixStack)
    }

    override fun drawForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        matrixStack.push()
        matrixStack.translate(-x.toDouble(), -y.toDouble(), 0.0)

        val rb = FlatColorRenderBuffer.SHARED
        depthClobberRenderState.apply()
        val windowHeight = Client.window.scaledHeight
        val windowWidth = Client.window.scaledWidth
        rb.pos(matrixStack, 0, windowHeight, 0).color(1f, 0f, 1f, 0.5f).endVertex()
        rb.pos(matrixStack, windowWidth, windowHeight, 0).color(1f, 0f, 1f, 0.5f).endVertex()
        rb.pos(matrixStack, windowWidth, 0, 0).color(1f, 0f, 1f, 0.5f).endVertex()
        rb.pos(matrixStack, 0, 0, 0).color(1f, 0f, 1f, 0.5f).endVertex()
        rb.draw(Primitive.QUADS)
        depthClobberRenderState.cleanup()

        facade.filterRendering { it.zIndex >= 1000 }
        facade.render(matrixStack)

        matrixStack.pop()
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

    override fun init() {
        Client.minecraft.keyboard.setRepeatEvents(true)
        super.init()
    }

    override fun removed() {
        super.removed()
        Client.minecraft.keyboard.setRepeatEvents(false)
    }

    override fun onClose() {
        super.onClose()
        facade.onClose()
    }

    public companion object {
        private val depthClobberRenderState = RenderState.normal
            .extend(DefaultRenderStates.DepthTest.ALWAYS, DefaultRenderStates.WriteMask.NO_COLOR)
    }
}
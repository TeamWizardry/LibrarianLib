package com.teamwizardry.librarianlib.gui.input


import com.mojang.blaze3d.platform.TextureUtil
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.toRl
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IFutureReloadListener
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import org.apache.commons.io.IOUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class Cursor(
    /**
     * The location of the cursor texture
     */
    val resourceLocation: ResourceLocation,
    /**
     * The position of the cursor point within the image (e.g. the tip of an arrow cursor). The origin is in the
     * top-left of the image, with the X axis extending to the right.
     */
    val originX: Int,
    /**
     * The position of the cursor point within the image (e.g. the tip of an arrow cursor). The origin is in the
     * top-left of the image, with the Y axis extending downward.
     */
    val originY: Int
): ISimpleReloadListener<Unit> {
    private var glfwCursor: Long = -1

    init {
        loadCursor()
        Client.resourceReloadHandler.register(this)
    }

    private fun loadCursor() {
        if(glfwCursor >= 0) {
            GLFW.glfwDestroyCursor(glfwCursor)
        }
        val stream = Client.resourceManager.getResource(resourceLocation).inputStream
        var bytebuffer: ByteBuffer? = null
        try {
            bytebuffer = TextureUtil.readResource(stream)
            bytebuffer.rewind()

            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val comp = stack.mallocInt(1)
                val pixels = STBImage.stbi_load_from_memory(bytebuffer, w, h, comp, 4)
                    ?: throw IOException("Could not load image: " + STBImage.stbi_failure_reason())
                val img = GLFWImage.mallocStack(stack)
                    .width(w[0])
                    .height(h[0])
                    .pixels(pixels)
                glfwCursor = GLFW.glfwCreateCursor(img, originX, originY)
            }
        } finally {
            MemoryUtil.memFree(bytebuffer)
            IOUtils.closeQuietly(stream)
        }
    }

    override fun prepare(resourceManager: IResourceManager, profiler: IProfiler) {
        // nop
    }

    override fun apply(result: Unit, resourceManager: IResourceManager, profiler: IProfiler) {
        loadCursor()
    }

    override fun toString(): String {
        return "Cursor($resourceLocation)"
    }

    @Suppress("unused")
    companion object {
        private fun _c(name: String, originX: Int, originY: Int) = Cursor("librarianlib:gui/textures/cursors/$name.png".toRl(), originX, originY)

        @JvmField
        val DEFAULT: Cursor? = null

        //region resize up/down/left/right
        /**
         * Arrow pointing up from a horizontal bar. Used to indicate upward limited movement of a divider
         */
        @JvmField
        val RESIZE_UP = _c("resize_up", 12, 12)
        /**
         * Arrow pointing down from a horizontal bar. Used to indicate downward limited movement of a divider
         */
        @JvmField
        val RESIZE_DOWN = _c("resize_down", 12, 12)
        /**
         * Arrow pointing left from a vertical bar. Used to indicate leftward limited movement of a divider
         */
        @JvmField
        val RESIZE_LEFT = _c("resize_left", 12, 12)
        /**
         * Arrow pointing right from a vertical bar. Used to indicate rightward limited movement of a divider
         */
        @JvmField
        val RESIZE_RIGHT = _c("resize_right", 12, 12)
        /**
         * Arrows pointing left and right from a vertical bar. Used to indicate horizontal movement of a divider
         */
        @JvmField
        val RESIZE_LEFTRIGHT = _c("resize_leftright", 12, 12)
        /**
         * Arrows pointing up and down from a horizontal bar. Used to indicate vertical movement of a divider
         */
        @JvmField
        val RESIZE_UPDOWN = _c("resize_updown", 12, 12)
        //endregion

        //region resize north/south/east/west
        /**
         * Arrow pointing up and to the right. Used to indicate resizing by dragging the top-right corner of an object
         */
        @JvmField
        val RESIZE_NE = _c("resize_ne", 10, 10)
        /**
         * Arrow pointing up and to the left. Used to indicate resizing by dragging the top-left corner of an object
         */
        @JvmField
        val RESIZE_NW = _c("resize_nw", 10, 10)
        /**
         * Arrow pointing down and to the right. Used to indicate resizing by dragging the bottom-right corner of an object
         */
        @JvmField
        val RESIZE_SE = _c("resize_se", 10, 10)
        /**
         * Arrow pointing down and to the left. Used to indicate resizing by dragging the bottom-left corner of an object
         */
        @JvmField
        val RESIZE_SW = _c("resize_sw", 10, 10)

        /**
         * Arrow pointing up. Used to indicate resizing by dragging the edge of an object upwards.
         */
        @JvmField
        val RESIZE_N = _c("resize_n", 10, 10)
        /**
         * Arrow pointing down. Used to indicate resizing by dragging the edge of an object downwards.
         */
        @JvmField
        val RESIZE_S = _c("resize_s", 10, 10)
        /**
         * Arrow pointing right. Used to indicate resizing by dragging the edge of an object rightwards.
         */
        @JvmField
        val RESIZE_E = _c("resize_e", 10, 10)
        /**
         * Arrow pointing left. Used to indicate resizing by dragging the edge of an object leftwards.
         */
        @JvmField
        val RESIZE_W = _c("resize_w", 10, 10)

        /**
         * Arrow pointing up and down. Used to indicate resizing by dragging the edge of an object up or down.
         */
        @JvmField
        val RESIZE_NS = _c("resize_ns", 10, 10)
        /**
         * Arrow pointing left and right. Used to indicate resizing by dragging the edge of an object left or right.
         */
        @JvmField
        val RESIZE_EW = _c("resize_ew", 10, 10)

        /**
         * Arrow pointing towards the top-right and bottom-left. Used to indicate resizing by dragging the corner of an
         * object up and to the right or down and to the left.
         */
        @JvmField
        val RESIZE_NESW = _c("resize_nesw", 10, 10)
        /**
         * Arrow pointing towards the top-left and bottom-right. Used to indicate resizing by dragging the corner of an
         * object up and to the left or down and to the right.
         */
        @JvmField
        val RESIZE_NWSE = _c("resize_nwse", 10, 10)
        // endregion

        /**
         * An arrow curving up and to the right. Indicates the creation of a link back to whatever is being dragged when
         * it is dropped, as opposed to moving it.
         */
        @JvmField
        val ALIAS = _c("alias", 15, 1)

        /**
         * A circle slash symbol. Indicates the inability to do something or interact with something
         */
        @JvmField
        val NO = _c("no", 7, 8)

        /**
         * A text insertion cursor. Indicates selectable or editable text
         */
        @JvmField
        val TEXT = _c("text", 3, 8)

        /**
         * Four arrows pointing in the cardinal directions. Indicates that whatever the cursor is over can be dragged
         * around
         */
        @JvmField
        val MOVE = _c("move", 8, 8)

        /**
         * A watch icon. Indicates a long-running process is underway
         */
        @JvmField
        val WAIT = _c("wait", 8, 8)

        /**
         * A crosshair cursor with a single transparent pixel under the mouse. Allows easily pinpointing a single pixel
         */
        @JvmField
        val SELECT_PIXEL = _c("select_pixel", 15, 15)

        /**
         * A thin cross, useful for fine mouse position control
         */
        @JvmField
        val CROSS = _c("cross", 11, 12)

        /**
         * A question mark icon. Indicates that when clicked, documentation will appear
         */
        @JvmField
        val HELP = _c("help", 8, 8)

        /**
         * A cursor with a small menu next to it. Indicates further options upon clicking
         */
        @JvmField
        val OPTION = _c("option", 1, 1)

        /**
         * A text insertion cursor for vertical text. Indicates selectable or editable text
         */
        @JvmField
        val TEXT_VERTICAL = _c("text_vertical", 8, 3)

        /**
         * A cursor with a green + icon below it. Indicates whatever is being dragged by the cursor will be added to
         * whatever is below the cursor, **without** removing it from where it came from
         */
        @JvmField
        val DROP_ADD = _c("drop_add", 1, 1)

        /**
         * A cursor with a white circle slash below it. Indicates whatever is being dragged by the cursor cannot be
         * dropped onto whatever is below the cursor.
         */
        @JvmField
        val DROP_NO = _c("drop_no", 1, 1)

        /**
         * A hand pointing with one finger. Indicates a clickable element
         */
        @JvmField
        val POINT = _c("point", 6, 1)

    }
}

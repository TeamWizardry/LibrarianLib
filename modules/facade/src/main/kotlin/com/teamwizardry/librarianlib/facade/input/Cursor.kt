package com.teamwizardry.librarianlib.facade.input


import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.ISimpleReloadListener
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.profiler.IProfiler
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

public class Cursor @JvmOverloads constructor(
    /**
     * The location of the cursor texture
     */
    private val resourceLocation: ResourceLocation,
    /**
     * The position of the cursor point within the image (e.g. the tip of an arrow cursor). The origin is in the
     * top-left of the image, with the X axis extending to the right.
     */
    private val originX: Int,
    /**
     * The position of the cursor point within the image (e.g. the tip of an arrow cursor). The origin is in the
     * top-left of the image, with the Y axis extending downward.
     */
    private val originY: Int,
    /**
     * The GLFW standard cursor ID, if any, otherwise a negative value
     */
    private val standardCursor: Int = -1
): ISimpleReloadListener<Unit> {
    private var glfwCursor: Long = -1

    init {
        loadCursor()
        Client.resourceReloadHandler.register(this)
    }

    private fun loadCursor() {
        if(glfwCursor > 0) {
            GLFW.glfwDestroyCursor(glfwCursor)
        }
        if(standardCursor >= 0) {
            glfwCursor = GLFW.glfwCreateStandardCursor(standardCursor)
            if(glfwCursor != 0L)
                return
        }
        val stream = Client.resourceManager.getResource(resourceLocation).inputStream
        var bytebuffer: ByteBuffer? = null
        try {
            bytebuffer = TextureUtil.readToBuffer(stream)
            bytebuffer!!.rewind()

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

    /**
     * Default cursors, based largely on those present in macOS. Guidelines on cursor usage largely sourced from the
     * macOS [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/macos/user-interaction/mouse-and-trackpad/#pointers)
     */
    @Suppress("unused")
    public companion object {
        private fun cursor(name: String, originX: Int, originY: Int, standardCursor: Int = -1)
            = Cursor(loc("librarianlib:facade/textures/cursors/$name.png"), originX, originY, standardCursor)

        /**
         * The default arrow cursor.
         *
         * Uses the standard system cursor if available.
         */
        @JvmField
        public val DEFAULT: Cursor = cursor("arrow", 1, 1, GLFW.GLFW_ARROW_CURSOR)

        //region resize up/down/left/right
        /**
         * Arrow pointing up from a horizontal bar. Used to indicate upward limited movement of a divider
         */
        @JvmField
        public val RESIZE_UP: Cursor = cursor("resize_up", 12, 12)
        /**
         * Arrow pointing down from a horizontal bar. Used to indicate downward limited movement of a divider
         */
        @JvmField
        public val RESIZE_DOWN: Cursor = cursor("resize_down", 12, 12)
        /**
         * Arrow pointing left from a vertical bar. Used to indicate leftward limited movement of a divider
         */
        @JvmField
        public val RESIZE_LEFT: Cursor = cursor("resize_left", 12, 12)
        /**
         * Arrow pointing right from a vertical bar. Used to indicate rightward limited movement of a divider
         */
        @JvmField
        public val RESIZE_RIGHT: Cursor = cursor("resize_right", 12, 12)
        /**
         * Arrows pointing left and right from a vertical bar. Used to indicate horizontal movement of a divider
         */
        @JvmField
        public val RESIZE_LEFTRIGHT: Cursor = cursor("resize_leftright", 12, 12)
        /**
         * Arrows pointing up and down from a horizontal bar. Used to indicate vertical movement of a divider
         */
        @JvmField
        public val RESIZE_UPDOWN: Cursor = cursor("resize_updown", 12, 12)
        //endregion

        //region resize north/south/east/west
        /**
         * Arrow pointing up and to the right. Used to indicate resizing by dragging the top-right corner of an object
         */
        @JvmField
        public val RESIZE_NE: Cursor = cursor("resize_ne", 10, 10)
        /**
         * Arrow pointing up and to the left. Used to indicate resizing by dragging the top-left corner of an object
         */
        @JvmField
        public val RESIZE_NW: Cursor = cursor("resize_nw", 10, 10)
        /**
         * Arrow pointing down and to the right. Used to indicate resizing by dragging the bottom-right corner of an object
         */
        @JvmField
        public val RESIZE_SE: Cursor = cursor("resize_se", 10, 10)
        /**
         * Arrow pointing down and to the left. Used to indicate resizing by dragging the bottom-left corner of an object
         */
        @JvmField
        public val RESIZE_SW: Cursor = cursor("resize_sw", 10, 10)

        /**
         * Arrow pointing up. Used to indicate resizing by dragging the edge of an object upwards.
         */
        @JvmField
        public val RESIZE_N: Cursor = cursor("resize_n", 10, 10)
        /**
         * Arrow pointing down. Used to indicate resizing by dragging the edge of an object downwards.
         */
        @JvmField
        public val RESIZE_S: Cursor = cursor("resize_s", 10, 10)
        /**
         * Arrow pointing right. Used to indicate resizing by dragging the edge of an object rightwards.
         */
        @JvmField
        public val RESIZE_E: Cursor = cursor("resize_e", 10, 10)
        /**
         * Arrow pointing left. Used to indicate resizing by dragging the edge of an object leftwards.
         */
        @JvmField
        public val RESIZE_W: Cursor = cursor("resize_w", 10, 10)

        /**
         * Arrow pointing up and down. Used to indicate resizing by dragging the edge of an object up or down.
         */
        @JvmField
        public val RESIZE_NS: Cursor = cursor("resize_ns", 10, 10)
        /**
         * Arrow pointing left and right. Used to indicate resizing by dragging the edge of an object left or right.
         */
        @JvmField
        public val RESIZE_EW: Cursor = cursor("resize_ew", 10, 10)

        /**
         * Arrow pointing towards the top-right and bottom-left. Used to indicate resizing by dragging the corner of an
         * object up and to the right or down and to the left.
         */
        @JvmField
        public val RESIZE_NESW: Cursor = cursor("resize_nesw", 10, 10)
        /**
         * Arrow pointing towards the top-left and bottom-right. Used to indicate resizing by dragging the corner of an
         * object up and to the left or down and to the right.
         */
        @JvmField
        public val RESIZE_NWSE: Cursor = cursor("resize_nwse", 10, 10)
        // endregion

        /**
         * An arrow curving up and to the right. Indicates the creation of a link back to whatever is being dragged when
         * it is dropped, as opposed to moving it.
         */
        @JvmField
        public val ALIAS: Cursor = cursor("alias", 15, 1)

        /**
         * A circle slash symbol. Indicates the inability to do something or interact with something
         */
        @JvmField
        public val NO: Cursor = cursor("no", 7, 8)

        /**
         * A text insertion cursor. Indicates selectable or editable text.
         *
         * Uses the standard system cursor if available.
         */
        @JvmField
        public val TEXT: Cursor = cursor("text", 3, 8, GLFW.GLFW_IBEAM_CURSOR)

        /**
         * Four arrows pointing in the cardinal directions. Indicates that whatever the cursor is over can be dragged
         * around
         */
        @JvmField
        public val MOVE: Cursor = cursor("move", 8, 8)

        /**
         * A watch icon. Indicates a long-running process is underway
         */
        @JvmField
        public val WAIT: Cursor = cursor("wait", 8, 8)

        /**
         * A crosshair cursor with a single transparent pixel under the mouse. Allows easily pinpointing a single pixel
         */
        @JvmField
        public val SELECT_PIXEL: Cursor = cursor("select_pixel", 15, 15)

        /**
         * A thin cross, useful for fine mouse position control
         *
         * Uses the standard system cursor if available.
         */
        @JvmField
        public val CROSS: Cursor = cursor("cross", 11, 12, GLFW.GLFW_CROSSHAIR_CURSOR)

        /**
         * A question mark icon. Indicates that when clicked, documentation will appear
         */
        @JvmField
        public val HELP: Cursor = cursor("help", 8, 8)

        /**
         * A cursor with a small menu next to it. Indicates further options upon clicking
         */
        @JvmField
        public val OPTION: Cursor = cursor("option", 1, 1)

        /**
         * A text insertion cursor for vertical text. Indicates selectable or editable text
         */
        @JvmField
        public val TEXT_VERTICAL: Cursor = cursor("text_vertical", 8, 3)

        /**
         * A cursor with a green + icon below it. Indicates whatever is being dragged by the cursor will be added to
         * whatever is below the cursor, **without** removing it from where it came from
         */
        @JvmField
        public val DROP_ADD: Cursor = cursor("drop_add", 1, 1)

        /**
         * A cursor with a white circle slash below it. Indicates whatever is being dragged by the cursor cannot be
         * dropped onto whatever is below the cursor.
         */
        @JvmField
        public val DROP_NO: Cursor = cursor("drop_no", 1, 1)

        /**
         * A hand pointing with one finger. Indicates a link.
         *
         * Uses the standard system cursor if available.
         */
        @JvmField
        public val POINT: Cursor = cursor("point", 6, 1, GLFW.GLFW_HAND_CURSOR)

        @JvmStatic
        public fun setCursor(cursor: Cursor?) {
            GLFW.glfwSetCursor(Client.window.handle, cursor?.glfwCursor ?: 0L)
        }
    }
}

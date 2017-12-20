package com.teamwizardry.librarianlib.features.utilities.client

import com.teamwizardry.librarianlib.features.kotlin.toRl
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Cursor
import java.nio.IntBuffer


class LibCursor(rl: ResourceLocation, originX: Int, originY: Int) {
    val lwjglCursor: Cursor

    init {
        val resource = Minecraft.getMinecraft().resourceManager.getResource(rl)
        val input = resource.inputStream
        val image = TextureUtil.readBufferedImage(input)

        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)

        val flippedPixels = IntArray(image.width * image.height)

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                flippedPixels[y * image.width + x] = pixels[(image.height - 1 - y) * image.width + x]
            }
        }

        val buf = IntBuffer.wrap(flippedPixels)

        lwjglCursor = Cursor(image.width, image.height, originX, originY, 1, buf, null)
    }

    @Suppress("unused")
    companion object {
        private fun _c(name: String, originX: Int, originY: Int) = LibCursor("librarianlib:textures/libgui/cursors/$name.png".toRl(), originX, originY)

        @JvmField
        val DEFAULT: LibCursor? = null

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
        val ALIAS = _c("alias", 15, 15)

        /**
         * A circle slash symbol. Indicates the inability to do something or interact with something
         */
        @JvmField
        val NO = _c("no", 7, 8)

        /**
         * A text insertion cursor. Indicates selectable or editable text
         */
        @JvmField
        val TEXT = _c("text", 3, 7)

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
        val OPTION = _c("option", 1, 15)

        /**
         * A text insertion cursor for vertical text. Indicates selectable or editable text
         */
        @JvmField
        val TEXT_VERTICAL = _c("text_vertical", 8, 13)

        /**
         * A cursor with a green + icon below it. Indicates whatever is being dragged by the cursor will be added to
         * whatever is below the cursor, **without** removing it from where it came from
         */
        @JvmField
        val DROP_ADD = _c("drop_add", 1, 23)

        /**
         * A cursor with a white circle slash below it. Indicates whatever is being dragged by the cursor cannot be
         * dropped onto whatever is below the cursor.
         */
        @JvmField
        val DROP_NO = _c("drop_no", 1, 23)

        /**
         * A hand pointing with one finger. Indicates a clickable element
         */
        @JvmField
        val POINT = _c("point", 6, 15)

    }
}

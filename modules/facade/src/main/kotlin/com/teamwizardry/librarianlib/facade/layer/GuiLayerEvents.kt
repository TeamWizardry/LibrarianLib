package com.teamwizardry.librarianlib.facade.layer

import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.math.Matrix3dStack
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.etcetera.eventbus.CancelableEvent

/**
 * ## Order of events:
 *
 * * **Input phase†:**
 * * Input events are fired (in the order they are received)
 * * **Game phase:**
 * * Minecraft ticks and renders the frame
 * * **Update phase:**
 * * Updates animations
 * * Fires [Update]
 * * **Layout phase:**
 * * Updates yoga layout‡
 * * Calls [GuiLayer.layoutChildren][GuiLayer.layoutChildren] and fires [LayoutChildren] for all dirty layers
 * * **Draw phase:**
 *
 * ## †Input notes:
 * [GLFW input guide](https://www.glfw.org/docs/latest/input_guide.html)
 *
 * Do note that there may be multiple events of a single type fired during the same frame, since events continue to be
 * queued between frames. For example, this is a possible sequence of events to receive in a single frame:
 * * `MouseMoved(pos=(100, 100))`
 * * `MouseDown(pos=(100, 100), button=0)`
 * * `MouseUp(pos=(100, 100), button=0)`
 * * `MouseMoved(pos=(120, 120))`
 * * `MouseDown(pos=(120, 120), button=0)`
 * * `MouseUp(pos=(120, 120), button=0)`
 *
 * Interestingly, the input phase is actually fired at the _end_ of the frame. I've placed it at the start in this list
 * because to an extent the end of one frame is the start of the next, and thinking of it as the start of the frame is
 * easier to reason about. However, in a few situations this distinction may be an important thing to keep in mind.
 *
 * ## ‡Yoga notes:
 * [Yoga layout](https://yogalayout.com/) support isn't currently implemented, but it is a strong possibility.
 */
object GuiLayerEvents {
    /**
     * Called after input events and before layout is calculated, this is the best place for general-purpose updates
     */
    class Update : Event()

    abstract class GuiInputEvent: Event()

    abstract class MouseEvent(val rootPos: Vec2d): GuiInputEvent() {
        val pos: Vec2d
            get() = stack.transform(rootPos)

        internal val stack = Matrix3dStack()
    }
    abstract class MovingMouseEvent(rootPos: Vec2d, val lastRootPos: Vec2d): MouseEvent(rootPos) {
        val lastPos: Vec2d
            get() = stack.transform(lastRootPos)
    }
    /**
     * Triggers when the mouse moves
     */
    class MouseMove(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse moves from off of this component to over this component
     */
    class MouseMoveOver(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse moves from over this component to off this component
     */
    class MouseMoveOff(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse button is pressed
     */
    class MouseDown(rootPos: Vec2d, val button: Int): MouseEvent(rootPos)

    /**
     * Triggers when the mouse button is released
     */
    class MouseUp(rootPos: Vec2d, val button: Int): MouseEvent(rootPos)

    /**
     * Triggers when the mouse button is pressed then released while over this component
     *
     * @see MouseRightClick
     * @see MouseOtherClick
     */
    class MouseClick(rootPos: Vec2d): MouseEvent(rootPos)

    /**
     * Triggers when the right mouse button is pressed then released while over this component
     *
     * @see MouseClick
     * @see MouseOtherClick
     */
    class MouseRightClick(rootPos: Vec2d): MouseEvent(rootPos)

    /**
     * Triggers when a button other than the right or left mouse button is pressed then released while over this component
     *
     * @see MouseClick
     * @see MouseRightClick
     */
    class MouseOtherClick(rootPos: Vec2d, val button: Int): MouseEvent(rootPos)

    /**
     * Triggers if the mouse is moved while a button is held
     */
    class MouseDrag(rootPos: Vec2d, lastRootPos: Vec2d, val button: Int): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse is scrolled. The [rootDelta] is a [Vec2d] because of 2d scroll wheels and because
     * [delta] may be off-axis if the layer itself is rotated.
     */
    class MouseScroll(rootPos: Vec2d, val rootDelta: Vec2d): MouseEvent(rootPos) {
        /**
         * The delta vector transformed into the current layer's coordinate space
         */
        val delta: Vec2d
            get() = stack.transformDelta(rootDelta)
    }

    abstract class KeyEvent: GuiInputEvent()
    class KeyDown(val keyCode: Int, val scanCode: Int, val modifiers: Int): KeyEvent()
    class KeyUp(val keyCode: Int, val scanCode: Int, val modifiers: Int): KeyEvent()
    class CharTyped(val codepoint: Char, val modifiers: Int): KeyEvent()

    /**
     * Fired before adding a child component
     */
    class AddChildEvent(val child: GuiLayer) : CancelableEvent()

    /**
     * Fired before removing a child component
     */
    class RemoveChildEvent(val child: GuiLayer) : CancelableEvent()

    /**
     * Fired before adding the component to a new parent
     */
    class AddToParentEvent(val parent: GuiLayer) : Event()

    /**
     * Fired before removing the component from its parent.
     */
    class RemoveFromParentEvent(val parent: GuiLayer) : Event()

    /**
     * Called to automatically lay out children
     */
    class LayoutChildren : Event()
}

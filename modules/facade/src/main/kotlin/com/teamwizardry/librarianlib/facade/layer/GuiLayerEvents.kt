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
 * * Fires [PrepareLayout]
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
 */
public object GuiLayerEvents {
    /**
     * Called after input events and before layout is calculated, this is where most state mutation should occur
     */
    public class Update: Event()

    /**
     * Called after [Update] and before layout is calculated, this is the best place to change the layout based on state
     * changes that may have occurred during the [Update] event.
     */
    public class PrepareLayout: Event()

    public abstract class GuiInputEvent: Event()

    public abstract class MouseEvent(public val rootPos: Vec2d): GuiInputEvent() {
        public val pos: Vec2d
            get() = stack.transform(rootPos)

        internal val stack = Matrix3dStack()
    }

    public abstract class MovingMouseEvent(rootPos: Vec2d, public val lastRootPos: Vec2d): MouseEvent(rootPos) {
        public val lastPos: Vec2d
            get() = stack.transform(lastRootPos)
    }

    /**
     * Triggers when the mouse moves
     */
    public class MouseMove(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse moves from off of this layer to over this layer
     */
    public class MouseMoveOver(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse moves from over this layer to off this layer
     */
    public class MouseMoveOff(rootPos: Vec2d, lastRootPos: Vec2d): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse button is pressed
     */
    public class MouseDown(rootPos: Vec2d, public val button: Int): MouseEvent(rootPos)

    /**
     * Triggers when the mouse button is released
     */
    public class MouseUp(rootPos: Vec2d, public val button: Int): MouseEvent(rootPos)

    /**
     * Triggers when the mouse button is pressed then released while over this layer
     *
     * @see MouseRightClick
     * @see MouseOtherClick
     */
    public class MouseClick(rootPos: Vec2d): MouseEvent(rootPos)

    /**
     * Triggers when the right mouse button is pressed then released while over this layer
     *
     * @see MouseClick
     * @see MouseOtherClick
     */
    public class MouseRightClick(rootPos: Vec2d): MouseEvent(rootPos)

    /**
     * Triggers when a button other than the right or left mouse button is pressed then released while over this layer
     *
     * @see MouseClick
     * @see MouseRightClick
     */
    public class MouseOtherClick(rootPos: Vec2d, public val button: Int): MouseEvent(rootPos)

    /**
     * Triggers if the mouse is moved while a button is held
     */
    public class MouseDrag(rootPos: Vec2d, lastRootPos: Vec2d, public val button: Int): MovingMouseEvent(rootPos, lastRootPos)

    /**
     * Triggers when the mouse is scrolled. The [rootDelta] is a [Vec2d] because of 2d scroll wheels and because
     * [delta] may be off-axis if the layer itself is rotated.
     */
    public class MouseScroll(rootPos: Vec2d, public val rootDelta: Vec2d): MouseEvent(rootPos) {
        /**
         * The delta vector transformed into the current layer's coordinate space
         */
        public val delta: Vec2d
            get() = stack.transformDelta(rootDelta)
    }

    public abstract class KeyEvent: GuiInputEvent()
    public class KeyDown(public val keyCode: Int, public val scanCode: Int, public val modifiers: Int): KeyEvent()
    public class KeyUp(public val keyCode: Int, public val scanCode: Int, public val modifiers: Int): KeyEvent()
    public class CharTyped(public val codepoint: Char, public val modifiers: Int): KeyEvent()

    /**
     * Fired before adding a child layer
     */
    public class AddChildEvent(public val child: GuiLayer): CancelableEvent()

    /**
     * Fired before removing a child layer
     */
    public class RemoveChildEvent(public val child: GuiLayer): CancelableEvent()

    /**
     * Fired before adding the layer to a new parent
     */
    public class AddToParentEvent(public val parent: GuiLayer): Event()

    /**
     * Fired before removing the layer from its parent.
     */
    public class RemoveFromParentEvent(public val parent: GuiLayer): Event()

    /**
     * Called to automatically lay out children
     */
    public class LayoutChildren: Event()
}

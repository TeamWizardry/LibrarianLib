package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.gui.component.supporting.*
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * The base class of every on-screen object. These can be nested within each other using [add]. Subcomponents will be
 * positioned relative to their parent, so modifications to the parent's [pos] will change their rendering position.
 *
 * # Summery
 *
 * - Events - Fire when something happens, allow you to change what happens or cancel it alltogether. Register on [BUS]
 * - Tags - Mark a component for retrieval later.
 * - Data - Store metadata in a component.
 *
 * # Detail
 *
 * ## Events
 *
 * More advanced functionality is achieved through event hooks on the component's [BUS]. All events are subclasses of
 * [Event] so a type hierarchy of that should show all events available to you. Only the child classes of [GuiLayer]
 * are fired by default, all others are either a part of a particular component class or require some action on the
 * user's part to initialize.
 *
 * ## Tags
 *
 * If you want to mark a component for retrieval later you can use [addTag] to add an arbitrary object as a tag.
 * Children with a specific tag can be retrieved later using [ComponentTagHandler.getByTag], or you can check if a component has a tag using
 * [hasTag]. Tags are stored in a HashSet, so any object that overrides the [hashCode] and [equals] methods will work by
 * value, but any object will work by identity. [Explanation here.](http://stackoverflow.com/a/1692882/1541907)
 *
 * ## Data
 *
 * If you need to store additional metadata in a component, this can be done with [setData]. The class passed in must be
 * the class of the data, and is used to reduce unchecked cast warnings and to ensure that the same key can be used with
 * multiple types of data. The key is used to allow multiple instances of the same data type to be stored in a component,
 * and is independent per class.
 * ```
 * component.setData(MyCoolObject.class, "foo", myInstance);
 * component.setData(MyCoolObject.class, "bar", anotherInstance);
 * component.setData(YourCoolObject.class, "foo", yourInstance);
 *
 * component.getData(MyCoolObject.class, "foo"); // => myInstance
 * component.getData(MyCoolObject.class, "bar"); // => anotherInstance
 * component.getData(YourCoolObject.class, "foo"); // => yourInstance
 * ```
 *
 */
@SideOnly(Side.CLIENT)
abstract class GuiLayer private constructor(
    posX: Int, posY: Int, width: Int, height: Int,
    internal val geometry: ComponentGeometryHandler = ComponentGeometryHandler(),
    internal val relationships: ComponentRelationshipHandler = ComponentRelationshipHandler(),
    internal val render: ComponentRenderHandler = ComponentRenderHandler(),
    internal val clipping: ComponentClippingHandler = ComponentClippingHandler()
)
    : IComponentGeometry by geometry, IComponentRelationship by relationships,
    IComponentRender by render, IComponentClipping by clipping
{
    @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0): this(
        posX, posY, width, height,
        ComponentGeometryHandler(),
        ComponentRelationshipHandler(),
        ComponentRenderHandler(),
        ComponentClippingHandler()
    )

    init {
        @Suppress("LeakingThis")
        {
            geometry.layer = this
            relationships.component = this
            render.layer = this
            clipping.component = this
        }()
    }

    fun preFrame() {
        this.BUS.fire(GuiLayerEvents.PreFrameEvent())
        this.children.forEach { it.preFrame() }
    }

    /**
     * Draws the component, this is called between pre and post draw events.
     *
     * The only guranteed GL state when this method is called is the following:
     *
     * - GL_TEXTURE_2D - enabled
     * - GL_COLOR - (1, 1, 1, 1)
     */
    open fun draw(partialTicks: Float) {}

    /**
     * Called to lay out the children of this layer. Unless overridden this is a no-op.
     *
     * This method is called before each frame if this component's bounds have changed, children are added/removed,
     * or [setNeedsLayout] has been called. After this method completes, the children of this component will be
     * checked for layout. This means that changes made in one layer can ripple downward.
     *
     * The idea behind this method is that self-contained components/layers can lay out their children dynamically
     * themselves. Examples of such a component would be a self-contained list item, a component that spaces out its
     * children equally along its length, or simply a component that needs to resize its background layer
     * to fit its dimensions.
     */
    open fun layOutChildren() {}

    /**
     * Calls [layOutChildren] then calls itself on this layer's children. [needsLayout] is reset to false _after_
     * [layOutChildren] completes, meaning size changes in that method won't cause a layout pass every frame.
     */
    open fun layOutChildrenIfNeeded() {
        if(needsLayout) {
            layOutChildren()
            BUS.fire(GuiLayerEvents.LayOutChildren())
            needsLayout = false
        }
        children.forEach { it.layOutChildrenIfNeeded() }
    }

    //region - Base component stuff
    @JvmField
    val BUS = EventBus()

    /**
     * Whether this component should be drawn or have events fire
     */
    open var isVisible = true

    /**
     * Returns true if the component is in need of a layout update. Defaults to true upon layer creation
     */
    open var needsLayout = true
        protected set

    /**
     * Marks this component to be laid out using [layOutChildren] before the next frame.
     */
    fun setNeedsLayout() {
        this.needsLayout = true
    }

    /**
     * Returns true if this component is invalid and it should be removed from its parent
     */
    open var isInvalid = false
        protected set
    internal fun clearInvalid() { this.isInvalid = false }

    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    fun invalidate() {
        this.isInvalid = true
    }
    //endregion

    /**
     * Transforms [pos] from `other`'s context (or the root context if null) to our context
     */
    fun otherPosToThisContext(other: GuiLayer?, pos: Vec2d)
        = geometry.thisPosToOtherContext(other, pos)

    //region - Internal
    init {
        this.pos = vec(posX, posY)
        this.size = vec(width, height)
    }
    //endregion

    open fun renderRoot(mousePos: Vec2d, partialTicks: Float) {
        StencilUtil.clear()
        GL11.glEnable(GL11.GL_STENCIL_TEST)
        cleanUpLayers()
        layOutChildrenIfNeeded()
        updateMouseBeforeRender(mousePos)
        geometry.calculateMouseOver(mousePos)
        preFrame()
        renderLayer(partialTicks)
        drawLate(partialTicks)
        GL11.glDisable(GL11.GL_STENCIL_TEST)
    }
}

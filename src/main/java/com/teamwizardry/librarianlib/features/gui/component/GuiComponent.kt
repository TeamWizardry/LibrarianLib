package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.supporting.*
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


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
 * [Event] so a type hierarchy of that should show all events available to you. Only the child classes of [GuiComponent]
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
abstract class GuiComponent private constructor(
    posX: Int, posY: Int, width: Int, height: Int,
    internal val data: ComponentDataHandler,
    internal val tags: ComponentTagHandler,
    internal val guiEventHandler: ComponentGuiEventHandler,
    internal val mouseHandler: ComponentMouseHandler
) : GuiLayer(posX, posY, width, height),
    IComponentData by data, IComponentTag by tags,
    IComponentGuiEvent by guiEventHandler, IComponentMouse by mouseHandler
{
    @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0): this(
        posX, posY, width, height,
        ComponentDataHandler(),
        ComponentTagHandler(),
        ComponentGuiEventHandler(),
        ComponentMouseHandler()
    )

    val subComponents: List<GuiComponent>
        get() = this.children.filterIsInstance<GuiComponent>()
    val rootComponent: GuiComponent
        get() = this.root as GuiComponent
    val parentComponent: GuiComponent?
        get() = this.parent as GuiComponent

    init {
        @Suppress("LeakingThis")
        {
            data.component = this
            tags.component = this
            guiEventHandler.component = this
        }()
    }
}

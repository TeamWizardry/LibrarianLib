package com.teamwizardry.librarianlib.features.facade.component

import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.facade.component.supporting.*
import com.teamwizardry.librarianlib.features.facade.components.LayerBackedComponent
import com.teamwizardry.librarianlib.features.facade.layers.ComponentBackedLayer
import com.teamwizardry.librarianlib.features.helpers.allDeclaredFields
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.reflect.Field
import java.util.IdentityHashMap
import kotlin.coroutines.CoroutineContext

/**
 * The fundamental building block of a LibrarianLib GUI. Generally a single unit of visual or organizational design.
 *
 * **Origins:**
 *
 * Vanilla GUIs very basic, where each frame you personally draw each texture and have to manually handle positioning
 * as the layout changes (though most likely it won't, because the math quickly becomes a pain). Vanilla does have the
 * [GuiButton][net.minecraft.client.gui.GuiButton], which isn't drawn or processed by you, however LibrarianLib's
 * layers and [components][GuiComponent] blow GuiButton clean out of the water with their versatility and ability to
 * easily create highly complex and dynamic interfaces.
 *
 * Over its evolution the GUI framework has been influenced largely by two things. It started out mostly inspired by
 * HTML's hierarchical nature, then later in its life it started to acquire many of the traits and structures from
 * Cocoa Touch. If you are familiar Cocoa Touch, many these concepts will be familiar to you, with layers and
 * components being CGLayer and UIView respectively.
 *
 * **Usage:**
 *
 * Layers are structured hierarchically, with each layer's children positioned in the layer's coordinate space.
 * Their relative transforms (position, rotation, and scale) can be accessed and modified using [pos], [rotation], and [scale].
 * Each layer implements [CoordinateSpace2D] and has methods to convert points between its local coordinate system and
 * another layer's local coordinate system.
 *
 * Each layer has its own event bus, which is used to hook into the many events fired by the GuiLayer/GuiComponent
 * itself or by one of its subclasses.
 *
 * Layers that have custom rendering (as opposed to consisting solely of other layers) can override the [draw] method
 * to draw their content.
 *
 * Note: The implementations of various responsibilities are in separate classes and implemented by GuiLayer via delegation.
 * This allows a large API to be accessible and overridable directly on the layer, while also allowing the various
 * responsibilities to stay separate.
 *
 * @see ILayerGeometry
 * @see ILayerRelationships
 * @see ILayerRendering
 * @see ILayerClipping
 * @See ILayerBase
 */
@SideOnly(Side.CLIENT)
open class GuiLayer private constructor(
    internal val geometry: LayerGeometryHandler,
    internal val relationships: LayerRelationshipHandler,
    internal val render: LayerRenderHandler,
    internal val clipping: LayerClippingHandler,
    internal val dataHandler: LayerDataHandler,
    internal val tagHandler: LayerTagHandler,
    internal val base: LayerBaseHandler
):
    ILayerGeometry by geometry, ILayerRelationships by relationships,
    ILayerRendering by render, ILayerClipping by clipping,
    ILayerData by dataHandler, ILayerTag by tagHandler,
    ILayerBase by base
{
    constructor(): this(0, 0, 0, 0)
    constructor(posX: Int, posY: Int): this(posX, posY, 0, 0)
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(
        LayerGeometryHandler(rect(posX, posY, width, height)),
        LayerRelationshipHandler(),
        LayerRenderHandler(),
        LayerClippingHandler(),
        LayerDataHandler(),
        LayerTagHandler(),
        LayerBaseHandler()
    )

    init {
        @Suppress("LeakingThis")
        {
            dataHandler.layer = this
            tagHandler.layer = this
            geometry.layer = this
            relationships.layer = this
            render.layer = this
            clipping.layer = this
            base.layer = this
        }()
    }

    /**
     * An optional internal display name that describes the role of this layer.
     *
     * This is not displayed to the user and is currently only used in [debugPrint], though may be utilized by future
     * debugging tools. It can be set in-line using the static method `GuiLayer.name(layer, "name")`.
     *
     * Examples: "Center stack", "Alternate color swatch", "Machine Top"
     */
    var name: String? = null

    /**
     * The event bus on which all events for this layer are fired.
     *
     * The built-in base events are located in [GuiLayerEvents] and [GuiComponentEvents]
     */
    @JvmField
    val BUS = EventBus()

    private var wrapper: LayerBackedComponent? = null

    override val parent: GuiLayer?
        get() = relationships.parent

    /**
     * ## !! Internal !! ##
     *
     * A method used to directly set this layer's parent. Use with _extreme_ care, as in not at all because you
     * will definitely break something. This is public so people who know what they're doing can use it.
     */
    open fun setParentInternal(value: GuiLayer?) {
        relationships.parent = value
    }

    override fun drawDebugBoundingBox() {
        val wrapper = wrapper
        if(wrapper != null) {
            wrapper.drawDebugBoundingBox()
        } else {
            render.drawDebugBoundingBox()
        }
    }

    /**
     * Wraps this layer in a GuiComponent
     */
    open fun componentWrapper(): GuiComponent {
        val wrapper = this.wrapper ?: LayerBackedComponent(this)
        this.wrapper = wrapper
        return wrapper
    }

    /**
     * Print this layer and its descendant hierarchy for debugging purposes.
     */
    fun debugPrint(detailed: Boolean = false): String {
        return debugLayerInField(null, detailed)
    }

    private fun debugLayerInField(field: String?, detailed: Boolean): String {
        val layer = when(this) {
            is LayerBackedComponent -> this.layer
            is ComponentBackedLayer -> this.component
            else -> this
        }

        val name = layer.name ?: name
        val infoStr = if(!detailed) null else
            layer.debugInfo().let {
                if(it.isEmpty())
                    null
                else
                    " {\n${it.joinToString("\n").prependIndent("    ")}\n  }"
            }

        var value = "" +
            "- ${layer.javaClass.simpleName}" +
            (if(detailed) "@${System.identityHashCode(layer).toString(16)}" else "") +
            (name?.let { " \"$it\"" } ?: "") +
            (field?.let { " [.$it]" } ?: "") +
            (infoStr ?: "")

        if(children.isNotEmpty()) {
            val fields = getLayerFields(layer)
            value += "\n" + children.joinToString("\n") { child ->
                child.debugLayerInField(fields[child], detailed).prependIndent("  ")
            }
        }
        return value
    }

    /**
     * Get debug information for [debugPrint]. Each list element will be placed on a separate line and will be
     * automatically indented.
     *
     * When overriding call `super.debugInfo()`, add to the returned list, then return it.
     */
    open fun debugInfo(): MutableList<String> {
        val list = mutableListOf<String>()
        list.add("pos = $pos, size = $size, anchor = $anchor, frame = $frame")
        listOfNotNull(
            if(scale2d == Vec2d.ZERO) null else "scale = $scale2d",
            if(rotation == 0.0) null else "rotation = $rotation",
            if(translateZ == 0.0) null else "translateZ = $translateZ",
            if(contentsOffset == Vec2d.ZERO) null else "contentsOffset = $contentsOffset",
            if(clipToBounds) "clip" else null
        ).also {
            if(it.isNotEmpty())
                list.add(it.joinToString(", "))
        }
        return list
    }

    override fun toString(): String {
        return "${this.javaClass.simpleName}@${System.identityHashCode(this).toString(16)}" +
            "($x, $y, $width, $height${name?.let { ", name=$name" } ?: ""})"
    }

    //region - Internal
    init {
        BUS.register(this)
    }
    //endregion

    companion object {
        @JvmStatic
        var showDebugTilt = false

        @JvmStatic
        var showDebugBoundingBox = false

        /**
         * The z index of tooltips. Overlays should not go above this level.
         */
        @JvmStatic
        val TOOLTIP_Z: Double = 1e11

        /**
         * In order to make an overlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        val OVERLAY_Z: Double = 1e10

        /**
         * In order to make a background layer, give the layer this [zIndex].
         */
        @JvmStatic
        val BACKGROUND_Z: Double = -1e9

        /**
         * In order to make an underlay layer, add a layer to the [root] with this [zIndex].
         */
        @JvmStatic
        val UNDERLAY_Z: Double = -1e10

        @JvmStatic
        var overrideDebugLineWidth: Float? = null


        private val layerFieldCache = mutableMapOf<Class<*>, List<Field>>()

        @JvmStatic
        private fun getLayerFields(obj: Any): Map<GuiLayer, String> {
            val fields = layerFieldCache.getOrPut(obj.javaClass) {
                obj.javaClass.allDeclaredFields.filter {
                    GuiLayer::class.java.isAssignableFrom(it.type)
                }.also {
                    it.forEach { it.isAccessible = true }
                }

            }

            return fields
                .flatMap {
                    val value = it.get(obj) as? GuiLayer
                        ?: return@flatMap emptyList<Pair<GuiLayer, String>>()
                    if(value is LayerBackedComponent)
                        return@flatMap listOf(value to it.name, value.layer to it.name)
                    if(value is ComponentBackedLayer)
                        return@flatMap listOf(value to it.name, value.component to it.name)
                    return@flatMap listOf(value to it.name )
                }
                .associateTo(IdentityHashMap()) { it }
        }

        /**
         * Set the layer's name inline without having to use a variable
         */
        @JvmStatic
        fun <T: GuiLayer> T.name(name: String): T {
            this.name = name
            return this
        }
    }
}

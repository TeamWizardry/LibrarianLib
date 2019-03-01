package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.gui.component.supporting.*
import com.teamwizardry.librarianlib.features.gui.components.LayerBackedComponent
import com.teamwizardry.librarianlib.features.gui.layers.ComponentBackedLayer
import com.teamwizardry.librarianlib.features.helpers.allDeclaredFields
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.reflect.Field
import java.util.IdentityHashMap
import kotlin.coroutines.CoroutineContext

@SideOnly(Side.CLIENT)
open class GuiLayer private constructor(
    internal val geometry: LayerGeometryHandler,
    internal val relationships: LayerRelationshipHandler,
    internal val render: LayerRenderHandler,
    internal val clipping: LayerClippingHandler,
    internal val base: LayerBaseHandler
)
    : ILayerGeometry by geometry, ILayerRelationships by relationships,
    ILayerRendering by render, ILayerClipping by clipping, ILayerBase by base, CoroutineScope {
    @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0): this(
        LayerGeometryHandler(rect(posX, posY, width, height)),
        LayerRelationshipHandler(),
        LayerRenderHandler(),
        LayerClippingHandler(),
        LayerBaseHandler()
    )
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Client

    init {
        @Suppress("LeakingThis")
        {
            geometry.layer = this
            relationships.component = this
            render.layer = this
            clipping.component = this
            base.layer = this
        }()
    }

    var name: String? = null

    @JvmField
    val BUS = EventBus()

    override var parent: GuiLayer?
        get() = relationships.parent
        internal set(value) { relationships.parent = value }

    private var wrapper: LayerBackedComponent? = null

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
     * When overriding simply call `super.debugInfo()`, add to the returned list, then return it.
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
            (name?.let { "(name=$name)" } ?: "")
    }

    //region - Internal
    init {
        BUS.register(this)
    }
    //endregion

    companion object {
        @JvmStatic
        var isDebugMode = false

        private val layerFieldCache = mutableMapOf<Class<*>, List<Field>>()

        @JvmStatic
        fun getLayerFields(obj: Any): Map<GuiLayer, String> {
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
    }
}

fun <T: GuiLayer> T.name(name: String): T {
    this.name = name
    return this
}
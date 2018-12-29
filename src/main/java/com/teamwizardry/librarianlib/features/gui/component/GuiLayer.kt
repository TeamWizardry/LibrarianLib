package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.gui.component.supporting.*
import com.teamwizardry.librarianlib.features.gui.components.LayerBackedComponent
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Client
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
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

    @JvmField
    val BUS = EventBus()

    override var parent: GuiLayer?
        get() = relationships.parent
        internal set(value) { relationships.parent = value }

    private var wrapper: LayerBackedComponent? = null

    /**
     * Wraps this layer in a GuiComponent
     */
    fun componentWrapper(): GuiComponent {
        val wrapper = this.wrapper ?: LayerBackedComponent(this)
        this.wrapper = wrapper
        return wrapper
    }

    //region - Internal
    init {
        ComponentEventHookAnnotSearcher.search(this)
    }
    //endregion

    companion object {
        @JvmStatic
        var isDebugMode = false
    }
}

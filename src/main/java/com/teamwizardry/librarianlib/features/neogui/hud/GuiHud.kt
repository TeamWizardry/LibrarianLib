package com.teamwizardry.librarianlib.features.neogui.hud

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.UUID

/**
 * Created by TheCodeWarrior
 */
object GuiHud {

    var root = StandaloneRootComponent {
        throw it
    }

    lateinit var all: AllHudElement private set
    lateinit var helmet: FullscreenHudElement private set
    lateinit var portal: FullscreenHudElement private set
    lateinit var crosshairs: CrosshairsHudElement private set
    lateinit var bossHealth: BossHealthHudElement private set
    lateinit var bossInfo: Map<UUID, BossInfoLayer> private set
    lateinit var armor: LeftStatusHudElement private set
    lateinit var health: LeftStatusHudElement private set
    lateinit var food: RightStatusHudElement private set
    lateinit var air: RightStatusHudElement private set
    lateinit var hotbar: HotbarHudElement private set
    lateinit var experience: ExperienceHudElement private set
    lateinit var text: FullscreenHudElement private set
    lateinit var healthMount: HealthMountHudElement private set
    lateinit var jumpBar: JumpBarHudElement private set
    lateinit var chat: ChatHudElement private set
    lateinit var playerList: PlayerListHudElement private set
    lateinit var sidebar: SidebarHudElement private set
    lateinit var debug: FullscreenHudElement private set
    lateinit var potionIcons: PotionIconsHudElement private set
    lateinit var subtitles: SubtitlesHudElement private set
    lateinit var fpsGraph: FpsGraphHudElement private set
    lateinit var vignette: FullscreenHudElement private set

    private lateinit var elements: Map<ElementType, HudElement>

    private val reloadListeners = mutableListOf<IHudReloadListener>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        reload()
        ClientRunnable.registerReloadHandler {
            reload()
        }
    }

    fun reload() {
        this.root = StandaloneRootComponent {
            throw it
        }

        this.all = AllHudElement()
        this.helmet = FullscreenHudElement(ElementType.HELMET)
        this.portal = FullscreenHudElement(ElementType.PORTAL)
        this.crosshairs = CrosshairsHudElement()
        this.bossHealth = BossHealthHudElement()
        this.bossInfo = bossHealth.bosses
        // v - still need attachment stacks defined
        this.armor = LeftStatusHudElement(ElementType.ARMOR) {
            ForgeHooks.getTotalArmorValue(Minecraft.getMinecraft().player) > 0
        }
        this.health = LeftStatusHudElement(ElementType.HEALTH) { true }
        this.food = RightStatusHudElement(ElementType.FOOD) { true }
        this.air = RightStatusHudElement(ElementType.AIR) {
            (Minecraft.getMinecraft().renderViewEntity as EntityPlayer).isInsideOfMaterial(Material.WATER)
        }
        this.hotbar = HotbarHudElement()
        this.experience = ExperienceHudElement()
        this.text = FullscreenHudElement(ElementType.TEXT)
        this.healthMount = HealthMountHudElement()
        this.jumpBar = JumpBarHudElement()
        this.chat = ChatHudElement()
        this.playerList = PlayerListHudElement() // finished
        this.sidebar = SidebarHudElement() // finished
        this.debug = FullscreenHudElement(ElementType.DEBUG)
        this.potionIcons = PotionIconsHudElement()
        this.subtitles = SubtitlesHudElement()
        this.fpsGraph = FpsGraphHudElement()
        this.vignette = FullscreenHudElement(ElementType.VIGNETTE)

        elements = mapOf(
            ElementType.ALL to all,
            ElementType.HELMET to helmet,
            ElementType.PORTAL to portal,
            ElementType.CROSSHAIRS to crosshairs,
            ElementType.BOSSHEALTH to bossHealth,
            ElementType.BOSSINFO to bossHealth, // there's no single boss info component (there's one for each boss bar)
            ElementType.ARMOR to armor,
            ElementType.HEALTH to health,
            ElementType.FOOD to food,
            ElementType.AIR to air,
            ElementType.HOTBAR to hotbar,
            ElementType.EXPERIENCE to experience,
            ElementType.TEXT to text,
            ElementType.HEALTHMOUNT to healthMount,
            ElementType.JUMPBAR to jumpBar,
            ElementType.CHAT to chat,
            ElementType.PLAYER_LIST to playerList,
            ElementType.DEBUG to debug,
            ElementType.POTION_ICONS to potionIcons,
            ElementType.SUBTITLES to subtitles,
            ElementType.FPS_GRAPH to fpsGraph,
            ElementType.VIGNETTE to vignette
        )

        root.add(*elements.values.toTypedArray())
        root.add(sidebar)

        reloadListeners.forEach { it.reloadHud() }
    }

    fun registerReloadListener(listener: IHudReloadListener) {
        reloadListeners.removeIf { it === listener }
        reloadListeners.add(listener)
    }

    fun element(type: ElementType): HudElement {
        return elements.getValue(type)
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun postEvent(e: RenderGameOverlayEvent.Post) {
        element(e.type).hudEvent(e)

        if(e.type == ElementType.CHAT) {
            sidebar.hudEvent(e)
        }

        if(e.type == ElementType.ALL) {
            Minecraft.getMinecraft().profiler.startSection("liblib")
            GlStateManager.enableBlend()
            GlStateManager.pushMatrix()

            GuiComponent.overrideDebugLineWidth = 1f
            root.renderRoot(ClientTickHandler.partialTicks, vec(0, 0))
            GuiComponent.overrideDebugLineWidth = null

            GlStateManager.popMatrix()
            GlStateManager.enableTexture2D()
            Minecraft.getMinecraft().profiler.endSection()
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun preEvent(e: RenderGameOverlayEvent.Pre) {
        if(e.type == ElementType.ALL) {
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            root.size_rm.set(vec(scaledResolution.scaledWidth, scaledResolution.scaledHeight))
            elements.values.forEach { it.isVisible = false }
        }

        if(e.type == ElementType.CHAT) {
            sidebar.hudEvent(e)
        }

        element(e.type).hudEvent(e)
    }
}

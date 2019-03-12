package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by TheCodeWarrior
 */
object GuiHud {

    val root = StandaloneRootComponent {
        throw it
    }

    val all = FullscreenHudElement(ElementType.ALL)
    val helmet = FullscreenHudElement(ElementType.HELMET)
    val portal = FullscreenHudElement(ElementType.PORTAL)
    val crosshairs = CrosshairsHudElement()
    val bossHealth = BossHealthHudElement()
    val bossInfo = bossHealth.bosses
    val armor = LeftStatusHudElement(ElementType.ARMOR) { true }
    val health = LeftStatusHudElement(ElementType.HEALTH) { true }
    val food = RightStatusHudElement(ElementType.FOOD) { true }
    val air = RightStatusHudElement(ElementType.AIR) {
        (Minecraft.getMinecraft().renderViewEntity as EntityPlayer).isInsideOfMaterial(Material.WATER)
    }
    val hotbar = HotbarHudElement()
    val experience = ExperienceHudElement()
    val text = FullscreenHudElement(ElementType.TEXT)
    val healthMount = HealthMountHudElement()
    val jumpBar = JumpBarHudElement()

    val chat = ChatHudElement(ElementType.CHAT)
    val playerList = PlayerListHudElement(ElementType.PLAYER_LIST)
    // val sidebar
    val debug = DebugHudElement(ElementType.DEBUG)
    val potionIcons = PotionIconsHudElement(ElementType.POTION_ICONS)
    val subtitles = SubtitlesHudElement(ElementType.SUBTITLES)
    val fpsGraph = FpsGraphHudElement(ElementType.FPS_GRAPH)

    val vignette = FullscreenHudElement(ElementType.VIGNETTE)


    private val elements = mapOf(
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

    init {
        MinecraftForge.EVENT_BUS.register(this)
        root.add(*elements.values.toTypedArray())
    }

    fun element(type: ElementType): HudElement {
        return elements.getValue(type)
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun postEvent(e: RenderGameOverlayEvent.Post) {
        element(e.type).hudEvent(e)

        if(e.type == ElementType.ALL) {
            Minecraft.getMinecraft().profiler.startSection("liblib")
            GlStateManager.enableBlend()
            GlStateManager.pushMatrix()

            root.renderRoot(ClientTickHandler.partialTicks, vec(0, 0))

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

        element(e.type).hudEvent(e)
    }
}

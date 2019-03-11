package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.StandaloneRootComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType
import net.minecraftforge.common.MinecraftForge
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

    val armor = ArmorHudElement()
    val health = HealthHudElement(ElementType.HEALTH)
    val food = FoodHudElement(ElementType.FOOD)
    val air = AirHudElement(ElementType.AIR)
    val hotbar = HotbarHudElement(ElementType.HOTBAR)
    val experience = ExperienceHudElement(ElementType.EXPERIENCE)
    val text = TextHudElement(ElementType.TEXT)
    val healthMount = HealthMountHudElement(ElementType.HEALTHMOUNT)
    val jumpBar = JumpBarHudElement(ElementType.JUMPBAR)
    val chat = ChatHudElement(ElementType.CHAT)
    val playerList = PlayerListHudElement(ElementType.PLAYER_LIST)
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
        ElementType.BOSSINFO to bossHealth,
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

    fun element(type: ElementType): GuiComponent {
        return elements.getValue(type)
    }

    @SubscribeEvent
    fun post(e: RenderGameOverlayEvent.Post) {
        when(e.type) {
            ElementType.ALL -> all.hudEvent(e)
            ElementType.HELMET -> helmet.hudEvent(e)
            ElementType.PORTAL -> portal.hudEvent(e)
            ElementType.CROSSHAIRS -> crosshairs.hudEvent(e)
            ElementType.BOSSHEALTH -> bossHealth.hudEvent(e)
            ElementType.BOSSINFO -> bossHealth.hudEvent(e)
            ElementType.ARMOR -> armor.hudEvent(e)
            ElementType.HEALTH -> health.hudEvent(e)
            ElementType.FOOD -> food.hudEvent(e)
            ElementType.AIR -> air.hudEvent(e)
            ElementType.HOTBAR -> hotbar.hudEvent(e)
            ElementType.EXPERIENCE -> experience.hudEvent(e)
            ElementType.TEXT -> text.hudEvent(e)
            ElementType.HEALTHMOUNT -> healthMount.hudEvent(e)
            ElementType.JUMPBAR -> jumpBar.hudEvent(e)
            ElementType.CHAT -> chat.hudEvent(e)
            ElementType.PLAYER_LIST -> playerList.hudEvent(e)
            ElementType.DEBUG -> debug.hudEvent(e)
            ElementType.POTION_ICONS -> potionIcons.hudEvent(e)
            ElementType.SUBTITLES -> subtitles.hudEvent(e)
            ElementType.FPS_GRAPH -> fpsGraph.hudEvent(e)
            ElementType.VIGNETTE -> vignette.hudEvent(e)
            null -> {}
        }

        if(e.type == ElementType.ALL) {
            GlStateManager.enableBlend()
            GlStateManager.pushMatrix()

            root.renderRoot(ClientTickHandler.partialTicks, vec(0, 0))

            GlStateManager.popMatrix()
            GlStateManager.enableTexture2D()
        }
    }

    fun pre(e: RenderGameOverlayEvent.Pre) {
        if(e.type == ElementType.ALL) {
            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())
            root.size_rm.set(vec(scaledResolution.scaledWidth, scaledResolution.scaledHeight))
        }

        when(e.type) {
            ElementType.ALL -> all.hudEvent(e)
            ElementType.HELMET -> helmet.hudEvent(e)
            ElementType.PORTAL -> portal.hudEvent(e)
            ElementType.CROSSHAIRS -> crosshairs.hudEvent(e)
            ElementType.BOSSHEALTH -> bossHealth.hudEvent(e)
            ElementType.BOSSINFO -> bossHealth.hudEvent(e)
            ElementType.ARMOR -> armor.hudEvent(e)
            ElementType.HEALTH -> health.hudEvent(e)
            ElementType.FOOD -> food.hudEvent(e)
            ElementType.AIR -> air.hudEvent(e)
            ElementType.HOTBAR -> hotbar.hudEvent(e)
            ElementType.EXPERIENCE -> experience.hudEvent(e)
            ElementType.TEXT -> text.hudEvent(e)
            ElementType.HEALTHMOUNT -> healthMount.hudEvent(e)
            ElementType.JUMPBAR -> jumpBar.hudEvent(e)
            ElementType.CHAT -> chat.hudEvent(e)
            ElementType.PLAYER_LIST -> playerList.hudEvent(e)
            ElementType.DEBUG -> debug.hudEvent(e)
            ElementType.POTION_ICONS -> potionIcons.hudEvent(e)
            ElementType.SUBTITLES -> subtitles.hudEvent(e)
            ElementType.FPS_GRAPH -> fpsGraph.hudEvent(e)
            ElementType.VIGNETTE -> vignette.hudEvent(e)
            null -> {}
        }
    }
}

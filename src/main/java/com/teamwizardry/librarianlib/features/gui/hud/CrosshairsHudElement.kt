package com.teamwizardry.librarianlib.features.gui.hud

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.IInventory
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderGameOverlayEvent

class CrosshairsHudElement: HudElement(RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
    val leftStack = StackLayout.build().horizontal().reverse().alignRight().alignCenterY().layer()
    val rightStack = StackLayout.build().horizontal().alignLeft().alignCenterY().layer()
    val bottomStack = StackLayout.build().vertical().alignTop().alignCenterX().layer()
    val topStack = StackLayout.build().vertical().reverse().alignBottom().alignCenterX().layer()

    val crosshair: GuiLayer = GuiLayer()
    val attackIndicator: GuiLayer = GuiLayer()
    val cooldownIndicator: GuiLayer = GuiLayer()
    val cooldownIndicatorFill: GuiLayer = GuiLayer()

    init {
        this.add(crosshair, attackIndicator, cooldownIndicator, cooldownIndicatorFill,
            leftStack, rightStack, bottomStack, topStack)
    }

    override fun hudEvent(e: RenderGameOverlayEvent.Pre) {
        super.hudEvent(e)
        val gamesettings = mc.gameSettings

        crosshair.frame = rect(root.widthi / 2 - 7, root.heighti / 2 - 7, 15, 15)
        leftStack.frame = rect(root.widthi / 2 - 7, root.heighti / 2, 0, 0)
        rightStack.frame = rect(root.widthi / 2 + 7, root.heighti / 2, 0, 0)
        topStack.frame = rect(root.widthi / 2, root.heighti / 2 - 7, 0, 0)
        bottomStack.frame = rect(root.widthi / 2, root.heighti / 2 + 7, 0, 0)

        attackIndicator.isVisible = false
        cooldownIndicator.isVisible = false
        cooldownIndicatorFill.isVisible = false

        val i = root.heighti / 2 - 7 + 16
        val j = root.widthi / 2 - 8
        attackIndicator.frame = rect(j, i, 16, 7) // the sprite is 16x16 in MC but visually is only 7px tall
        cooldownIndicator.frame = rect(j, i, 16, 4)
        cooldownIndicatorFill.frame = rect(j, i, 16, 4)

        if (gamesettings.thirdPersonView == 0) {
            if (mc.playerController.isSpectator && mc.pointedEntity == null) {
                val raytraceresult = mc.objectMouseOver

                if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                    this.isVisible = false
                }

                val blockpos = raytraceresult.blockPos

                val state = mc.world.getBlockState(blockpos)
                if (!state.block.hasTileEntity(state) || mc.world.getTileEntity(blockpos) !is IInventory) {
                    this.isVisible = false
                }
            }

            if (gamesettings.showDebugInfo && !gamesettings.hideGUI && !mc.player.hasReducedDebug() && !gamesettings.reducedDebugInfo) {
//                GlStateManager.pushMatrix()
//                GlStateManager.translate((width / 2).toFloat(), (height / 2).toFloat(), this.zLevel)
//                val entity = this.mc.renderViewEntity
//                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0f, 0.0f, 0.0f)
//                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0f, 1.0f, 0.0f)
//                GlStateManager.scale(-1.0f, -1.0f, -1.0f)
//                OpenGlHelper.renderDirections(10)
//                GlStateManager.popMatrix()
            } else {
                if (mc.gameSettings.attackIndicator == 1) {
                    val f = mc.player.getCooledAttackStrength(0.0f)
                    var flag = false

                    if (mc.pointedEntity != null && mc.pointedEntity is EntityLivingBase && f >= 1.0f) {
                        flag = mc.player.cooldownPeriod > 5.0f
                        flag = flag and (mc.pointedEntity as EntityLivingBase).isEntityAlive
                    }

                    if (flag) {
                        attackIndicator.isVisible = true
                    } else if (f < 1.0f) {
                        cooldownIndicator.isVisible = true
                        cooldownIndicatorFill.isVisible = true
                        cooldownIndicatorFill.widthi = (f * 17.0f).toInt()
                    }
                }
            }
        }
    }
}
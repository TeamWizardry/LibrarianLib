package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

@Deprecated("Use a ComponentSprite and specify caps in the mcmeta file")
class ComponentSpriteProgressBar @JvmOverloads constructor(var sprite: ISprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiComponent(x, y, width, height) {

    class AnimationLoopEvent(val component: ComponentSpriteProgressBar) : Event()
    enum class ProgressDirection { Y_POS, Y_NEG, X_POS, X_NEG }

    var direction = Option<ComponentSpriteProgressBar, ProgressDirection>(ProgressDirection.Y_POS)
    var progress = Option<ComponentSpriteProgressBar, Float>(1f)
    var depth = Option<ComponentSpriteProgressBar, Boolean>(true)
    var color = Option<ComponentSpriteProgressBar, Color>(Color.WHITE)

    var lastAnim: Int = 0

    private var progressCache: Float = 1f
    private var dir: ProgressDirection = ProgressDirection.Y_POS
    private val spriteWrapper = object : ISprite {
        override fun bind() {
            sprite!!.bind()
        }

        override fun minU(animFrames: Int): Float {
            return when(dir) {
                ProgressDirection.X_NEG -> {
                    val min = sprite!!.minU(animFrames)
                    val max = sprite!!.maxU(animFrames)
                    max - (max - min) * progressCache
                }
                else -> {
                    sprite!!.minU(animFrames)
                }
            }
        }

        override fun minV(animFrames: Int): Float {
            return when(dir) {
                ProgressDirection.Y_NEG -> {
                    val min = sprite!!.minV(animFrames)
                    val max = sprite!!.maxV(animFrames)
                    max - (max - min) * progressCache
                }
                else -> {
                    sprite!!.minV(animFrames)
                }
            }
        }

        override fun maxU(animFrames: Int): Float {
            return when(dir) {
                ProgressDirection.X_POS -> {
                    val min = sprite!!.minU(animFrames)
                    val max = sprite!!.maxU(animFrames)
                    min + (max - min) * progressCache
                }
                else -> {
                    sprite!!.maxU(animFrames)
                }
            }
        }

        override fun maxV(animFrames: Int): Float {
            return when(dir) {
                ProgressDirection.Y_POS -> {
                    val min = sprite!!.minV(animFrames)
                    val max = sprite!!.maxV(animFrames)
                    min + (max - min) * progressCache
                }
                else -> {
                    sprite!!.maxV(animFrames)
                }
            }
        }

        override fun draw(animTicks: Int, x: Float, y: Float) {
            sprite!!.draw(animTicks, x, y)
        }

        override fun draw(animTicks: Int, x: Float, y: Float, width: Float, height: Float) {
            sprite!!.draw(animTicks, x, y, width, height)
        }

        override val width: Int get() = sprite!!.width
        override val height: Int get() = sprite!!.height
        override val uSize: Float get() = sprite!!.uSize
        override val vSize: Float get() = sprite!!.vSize
        override val frameCount: Int get() = sprite!!.frameCount
        override val minUCap: Float get() = sprite!!.minUCap
        override val minVCap: Float get() = sprite!!.minVCap
        override val maxUCap: Float get() = sprite!!.maxUCap
        override val maxVCap: Float get() = sprite!!.maxVCap
        override val pinTop: Boolean get() = dir != ProgressDirection.Y_NEG
        override val pinBottom: Boolean get() = dir != ProgressDirection.Y_POS
        override val pinLeft: Boolean get() = dir != ProgressDirection.X_NEG
        override val pinRight: Boolean get() = dir != ProgressDirection.X_POS
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val alwaysTop = !depth.getValue(this)
        val sp = sprite ?: return
        val animationTicks = animator.time.toInt()



        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_ALWAYS)

        if (sp.frameCount > 0 && lastAnim / sp.frameCount < animationTicks / sp.frameCount)
            BUS.fire(AnimationLoopEvent(this))

        lastAnim = animationTicks
        color.getValue(this).glColor()
        sp.bind()

        var w = size.xi
        var h = size.yi
        dir = direction.getValue(this)
        progressCache = this.progress.getValue(this)

        if (dir == ProgressDirection.Y_POS || dir == ProgressDirection.Y_NEG)
            h = (h * progressCache).toInt()
        if (dir == ProgressDirection.X_POS || dir == ProgressDirection.X_NEG)
            w = (w * progressCache).toInt()

        sp.draw(animationTicks, size.xf-w, size.yf-h, w.toFloat(), h.toFloat())
        
        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_LESS)
    }

}

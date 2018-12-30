package com.teamwizardry.librarianlib.test.particlesystem

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentColorPicker
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.*
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.ConditionalUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.GUIPhysicsModule3D
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt

class GuiParticleMaker : GuiBase() {

    enum class View {
        EMPTY, PARTICLE, SIMULATION
    }

    var lastPitchYaw = Vec2d.ZERO
    var pitchYaw = Vec2d.ZERO
    var postMouse = Vec2d.ZERO
    var preMouse = Vec2d.ZERO
    var renderBoxMouseDown = false
    var colorWheelMouseDown = false
    var colorWheelCursor = Vec2d.ZERO

    var system = PhysicsCurtainSystem

    var colorPrimary = Color.WHITE
    var colorSecondary = Color.WHITE
    var colorFading = false
    var lifetime = 50
    var particleCount = 1
    var continuousParticles = true
    var gravity = 0.4
    var enableCollision = false
    var bounciness = 0.8f
    var damping = 0.01f
    var friction = 0.01f
    var velX = 0.0
    var velY = 0.0
    var velZ = 0.0
    var maxVelX = 0.0
    var maxVelY = 0.0
    var maxVelZ = 0.0
    var randomVelocity = false

    init {
        system.reload()

        val background = PastryBackground(BackgroundTexture.BLACK, -500 / 2, -300 / 2, 500, 300)
        main.add(background.componentWrapper())

        val renderBox = ComponentRect((-500 / 2) + 10, (-300 / 2) + 10, 300 - 20, 300 - 20)
        renderBox.zIndex = 2.0
        renderBox.cursor = LibCursor.MOVE
        renderBox.color = Color.BLACK

        renderBox.clipToBounds = true

        renderBox.BUS.hook<GuiComponentEvents.ComponentTickEvent> {
            if (continuousParticles)
                spawn()
        }

        renderBox.BUS.hook<GuiComponentEvents.MouseDownEvent> {
            if (renderBox.mouseOver) {
                renderBoxMouseDown = true
                preMouse = main.mousePos
            }
        }

        renderBox.BUS.hook<GuiComponentEvents.MouseDragEvent> {
            if (renderBoxMouseDown) {
                postMouse = main.mousePos
                val sub = postMouse.sub(preMouse)
                pitchYaw = sub.add(lastPitchYaw)
            }
        }

        renderBox.BUS.hook<GuiComponentEvents.MouseUpEvent> {
            renderBoxMouseDown = false
            lastPitchYaw = pitchYaw
        }

        renderBox.BUS.hook<GuiLayerEvents.PostDrawEvent> {
            // RENDER AXIS THING
            GlStateManager.pushMatrix()
            GlStateManager.translate(10.0, renderBox.height - 10, 100.0)
            GlStateManager.rotate(pitchYaw.y.toFloat(), -1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(pitchYaw.x.toFloat(), 0.0f, 1.0f, 0.0f)
            GlStateManager.scale(-1.0f, -1.0f, -1.0f)
            OpenGlHelper.renderDirections(10)
            GlStateManager.popMatrix()
            // RENDER AXIS THING


            // RENDER PARTICLE SYSTEM
            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()

            GlStateManager.alphaFunc(GL11.GL_GREATER, 1 / 256f)
            GlStateManager.disableLighting()
            GlStateManager.translate(renderBox.width / 2.0, renderBox.height / 2.0, 100.0)

            GlStateManager.rotate(pitchYaw.y.toFloat(), 1.0f, 0.0f, 0.0f)
            GlStateManager.rotate(pitchYaw.x.toFloat(), 0.0f, -1.0f, 0.0f)

            system.render()

            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
            // RENDER PARTICLE SYSTEM
        }
        main.add(renderBox)

        val tabPane = PastryTabPane(500 - 190 - 10, 10, 190, 300 - 20)
        background.componentWrapper().add(tabPane)


        val spawning = tabPane.addTab("Spawning")
        spawning.isVisible = true


        // --- CONTINUOUS SPAWNING --- //
        val continuousSpawning = makeSwitch("Manually spawn particles", 0, 0)

        val animatableWrapper1 = GuiComponent(0, continuousSpawning.yi + 10)

        val spawnWhileHoldingButton = makeSwitch("Spawn continuously while pressing", 0, continuousSpawning.yi + 10)
        spawnWhileHoldingButton.isVisible = false

        val spawnButton = PastryButton("Spawn", 0, spawnWhileHoldingButton.yi + 10, 50)
        spawnButton.isVisible = false

        var holdingDown = false
        spawnButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            spawn()
        }
        spawnButton.BUS.hook<GuiComponentEvents.MouseDownEvent> {
            if (spawnButton.mouseOver && spawnWhileHoldingButton.state) {
                holdingDown = true
            }
        }
        spawnButton.BUS.hook<GuiComponentEvents.MouseUpEvent> {
            holdingDown = false
        }
        spawnButton.BUS.hook<GuiComponentEvents.ComponentTickEvent> {
            if (holdingDown) spawn()
        }

        continuousSpawning.BUS.hook<PastryToggle.StateChangeEvent> {
            continuousParticles = continuousSpawning.state
            spawnButton.isVisible = !continuousParticles
            spawnWhileHoldingButton.isVisible = !continuousParticles

            if (!continuousParticles) {
                animatableWrapper1.pos_rm.animate(Vec2d(0.0, spawnButton.y + spawnButton.height), 10f, Easing.easeOutQuart)
            } else {
                animatableWrapper1.pos_rm.animate(Vec2d(0.0, continuousSpawning.y + 10), 10f, Easing.easeOutQuart)
            }
        }
        spawning.add(continuousSpawning, spawnWhileHoldingButton, spawnButton, animatableWrapper1)
        // --- CONTINUOUS SPAWNING --- //


        // --- LIFE TIME --- //
        val sliderLifetime = makeSlider(
                text = "Set Particle Lifetime",
                y = 10,
                beginValue = lifetime,
                range = 1 .. 500) {
            lifetime = it
        }
        animatableWrapper1.add(sliderLifetime)
        // --- LIFE TIME --- //


        // --- PARTICLE COUNT --- //
        val sliderParticleCount = makeSlider(
                text = "Set Particle Count",
                y = sliderLifetime.yi + 20,
                beginValue = 1,
                range = 1 .. 1000) {
            particleCount = it
        }

        animatableWrapper1.add(sliderParticleCount)
        // --- PARTICLE COUNT --- //


        val aesthetics = tabPane.addTab("Aesthetics")


        val mainRadius = 30.0
        val colorWheelPrimary = ComponentColorPicker(aesthetics.widthi / 2 - (mainRadius.toInt() * 2) / 2, 0, (mainRadius.toInt() * 2), (mainRadius.toInt() * 2))
        colorWheelPrimary.BUS.hook<ComponentColorPicker.ColorChangeEvent> {
            colorPrimary = it.color
        }

        val colorWheelSecondary = ComponentColorPicker(aesthetics.widthi / 2 - (mainRadius.toInt() * 2) / 2, 0, (mainRadius.toInt() * 2), (mainRadius.toInt() * 2))
        colorWheelSecondary.isVisible = false
        colorWheelSecondary.BUS.hook<ComponentColorPicker.ColorChangeEvent> {
            colorSecondary = it.color
        }
        aesthetics.add(colorWheelPrimary, colorWheelSecondary)

        // COLOR FADING
        val colorFade = makeSwitch("Enable Color Fading", 0, mainRadius.toInt() * 2 + 10) {
            colorFading = !it
            if (!it) {
                colorWheelPrimary.pos_rm.animate(Vec2d.ZERO, 20f, Easing.easeOutQuart)

                colorWheelSecondary.isVisible = true
                colorWheelSecondary.pos_rm.animate(Vec2d(aesthetics.width - (mainRadius * 2), 0.0), 20f, Easing.easeOutQuart)
            } else {
                colorWheelPrimary.pos_rm.animate(Vec2d(aesthetics.width / 2.0 - (mainRadius * 2.0) / 2.0, 0.0), 20f, Easing.easeOutQuart)

                colorWheelSecondary.pos_rm.animate(Vec2d(aesthetics.width / 2.0 - (mainRadius * 2.0) / 2.0, 0.0), 20f, Easing.easeOutQuart).completion = Runnable {
                    colorWheelSecondary.isVisible = false
                }
            }
        }
        aesthetics.add(colorFade)


        val physics = tabPane.addTab("Physics")


        physics.add(makeSwitch("Enable Collision", 0, 0) { enableCollision = it })


        val animatableWrapper2 = GuiComponent(0, 30)
        physics.add(animatableWrapper2)

        val velXField = ComponentDescriptiveNumField("X", velX, 40, 40, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velX = it
        }
        velXField.isVisible = false
        val velYField = ComponentDescriptiveNumField("Y", velY, 100, 40, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velY = it
        }
        velYField.isVisible = false
        val velZField = ComponentDescriptiveNumField("Z", velZ, 150, 40, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velZ = it
        }
        velZField.isVisible = false
        physics.add(velXField, velYField, velZField)


        val maxVelXField = ComponentDescriptiveNumField("Max X", maxVelX, 40, 65, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velX = it
        }
        maxVelXField.isVisible = false
        val maxVelYField = ComponentDescriptiveNumField("Max Y", maxVelY, 100, 65, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velY = it
        }
        maxVelYField.isVisible = false
        val maxVelZField = ComponentDescriptiveNumField("Max Z", maxVelZ, 165, 50, 20, Minecraft().fontRenderer.FONT_HEIGHT + 2) {
            velZ = it
        }
        maxVelZField.isVisible = false
        physics.add(maxVelXField, maxVelYField, maxVelZField)


        val randomVelocitySwitch = makeSwitch("Randomized Velocity", 10, 20) {
            if (!it) {

                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 80.0), 10f, Easing.easeOutQuart).completion = Runnable {
                    velXField.updateText("Min X")
                    velYField.updateText("Min Y")
                    velZField.updateText("Min Z")

                    maxVelXField.isVisible = true
                    maxVelYField.isVisible = true
                    maxVelZField.isVisible = true
                }
            } else {
                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 60.0), 10f, Easing.easeOutQuart)
                velXField.updateText("X")
                velYField.updateText("Y")
                velZField.updateText("Z")

                maxVelXField.isVisible = false
                maxVelYField.isVisible = false
                maxVelZField.isVisible = false
            }
        }
        randomVelocitySwitch.isVisible = false
        physics.add(randomVelocitySwitch)

        physics.add(makeSwitch("Set Velocity", 0, 10) {
            if (!it) {
                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 60.0), 10f, Easing.easeOutQuart).completion = Runnable {
                    velXField.isVisible = true
                    velYField.isVisible = true
                    velZField.isVisible = true
                    randomVelocitySwitch.isVisible = true
                    randomVelocity = true
                }
            } else {
                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 30.0), 10f, Easing.easeOutQuart)
                velXField.isVisible = false
                velYField.isVisible = false
                velZField.isVisible = false
                randomVelocitySwitch.isVisible = false
                randomVelocity = false
            }
        })


        animatableWrapper2.add(makeSlider("Gravity", 0, gravity, 0.0 .. 10.0) { gravity = it })
        animatableWrapper2.add(makeSlider("Damping", 20, damping.toDouble(), 0.0 .. 1.0) { damping = it.toFloat() })
        animatableWrapper2.add(makeSlider("Friction", 40, friction.toDouble(), 0.0 .. 1.0) { friction = it.toFloat() })
        animatableWrapper2.add(makeSlider("Bounciness", 60, bounciness.toDouble(), 0.0 .. 1.0) { bounciness = it.toFloat() })
    }

    private fun makeSlider(text: String, y: Int, beginValue: Double, range: ClosedRange<Double>, stateChange: (newProgress: Double) -> Unit): PastrySlider {
        val titleLayer = TextLayer(0, -10, 0, 0)
        titleLayer.fitToText = true
        titleLayer.text = text

        val slider = PastrySlider(0, y + 10, 150, false, Cardinal2d.GUI.DOWN)
        slider.range = range
        slider.value = beginValue
        slider.BUS.fire(PastryToggle.StateChangeEvent())

        val numberLayer = TextLayer(150 + 10, 0, 0, 0)
        numberLayer.fitToText = true
        numberLayer.text = "${(slider.value * 100).roundToInt() / 100.0}"
        slider.BUS.hook<PastrySlider.ValueChangeEvent> { event ->
            val result = (event.newValue * 100).roundToInt() / 100.0
            numberLayer.text = "$result"
            stateChange(result)
        }

        slider.add(titleLayer, numberLayer)

        return slider
    }

    private fun makeSlider(text: String, y: Int, beginValue: Int, range: ClosedRange<Int>, stateChange: (newProgress: Int) -> Unit): PastrySlider {
        val titleLayer = TextLayer(0, -10, 0, 0)
        titleLayer.fitToText = true
        titleLayer.text = text

        val slider = PastrySlider(0, y + 10, 150, false, Cardinal2d.GUI.DOWN)
        slider.range = range.start.toDouble() .. range.endInclusive.toDouble()
        slider.value = beginValue.toDouble()
        slider.BUS.fire(PastryToggle.StateChangeEvent())

        val numberLayer = TextLayer(150 + 10, 0, 0, 0)
        numberLayer.fitToText = true
        numberLayer.text = "$beginValue"
        var lastValue = beginValue
        slider.BUS.hook<PastrySlider.ValueChangeEvent> { event ->
            val intValue = event.newValue.roundToInt()
            event.newValue = intValue.toDouble()
            if(intValue != lastValue) {
                numberLayer.text = "$intValue"
                stateChange(intValue)
                lastValue = intValue
            }
        }

        slider.add(titleLayer, numberLayer)

        return slider
    }

    private fun makeSwitch(text: String, x: Int = 0, y: Int, stateChange: (newState: Boolean) -> Unit = {}): PastrySwitch {
        val switch = PastrySwitch(x, y)
        switch.BUS.hook<PastryToggle.StateChangeEvent> {
            stateChange(switch.state)
        }
        val continuousSpawningDescription = TextLayer(switch.widthi + 5, 0, 0, 0)
        continuousSpawningDescription.fitToText = true
        continuousSpawningDescription.text = text
        switch.add(continuousSpawningDescription)
        return switch
    }

    private fun spawn() {
        for (i in 0 until particleCount)
            system.spawn(
                    lifetime = lifetime.toDouble(),
                    size = 16.0, pos = Vec3d(0.0, 10.0, 0.0),

                    bounciness = bounciness,
                    friction = friction,
                    dampness = damping,
                    gravity = gravity,
                    collision = if (enableCollision) 1 else 0,

                    colorFading = if (colorFading) 1 else 0,
                    colorPrimary = colorPrimary,
                    colorSecondary = colorSecondary)
    }

    override fun doesGuiPauseGame(): Boolean = false

    object PhysicsCurtainSystem : ParticleSystem() {

        init {
            manuallyRender = true
        }

        override fun configure() {
            val size = bind(1)
            val position = bind(3)
            val previousPosition = bind(3)
            val velocity = bind(3)

            val bounciness = bind(1)
            val friction = bind(1)
            val dampness = bind(1)
            val gravity = bind(1)
            val collision = bind(1)

            val enableColorFading = bind(1)
            val colorMain = bind(4)
            val colorPrimary = bind(4)
            val colorSecondary = bind(4)

            updateModules.add(GUIPhysicsModule3D(
                    position,
                    previousPosition,
                    velocity = velocity,
                    gravityBinding = gravity,
                    enableCollisionBinding = collision,
                    bouncinessBinding = bounciness,
                    dampingBinding = dampness,
                    frictionBinding = friction
            ))

            updateModules.add(ConditionalUpdateModule(SetValueUpdateModule(colorMain,
                    EaseBinding(lifetime, age, easing = Easing.linear, bindingSize = 4, origin = colorPrimary, target = colorSecondary)
            )) {
                enableColorFading.load(it)
                enableColorFading.getValue(0).toInt() != 0
            })

            renderModules.add(SpriteRenderModule(
                    sprite = ResourceLocation("minecraft", "textures/items/clay_ball.png"),
                    enableBlend = true,
                    blendMode = BlendMode.ADDITIVE,
                    previousPosition = previousPosition,
                    position = position,
                    color = colorMain,
                    size = size,
                    depthMask = false,
                    is2D = true
            ))
        }

        fun spawn(lifetime: Double,
                  size: Double,
                  pos: Vec3d,

                  bounciness: Float,
                  friction: Float,
                  dampness: Float,
                  gravity: Double,
                  collision: Int,

                  colorFading: Int,
                  colorPrimary: Color,
                  colorSecondary: Color = colorPrimary) {
            this.addParticle(lifetime,
                    size,
                    pos.x, pos.y, pos.z,
                    pos.x, pos.y, pos.z,
                    RandomUtils.nextDouble(0.0, 2.0) - 1.0, RandomUtils.nextDouble(4.0, 7.0), RandomUtils.nextDouble(0.0, 2.0) - 1.0,

                    bounciness.toDouble(),
                    friction.toDouble(),
                    dampness.toDouble(),
                    gravity,
                    collision.toDouble(),

                    colorFading.toDouble(),
                    colorPrimary.red.toDouble() / 255.0, colorPrimary.green.toDouble() / 255.0, colorPrimary.blue.toDouble() / 255.0, colorPrimary.alpha.toDouble() / 255.0,
                    colorPrimary.red.toDouble() / 255.0, colorPrimary.green.toDouble() / 255.0, colorPrimary.blue.toDouble() / 255.0, colorPrimary.alpha.toDouble() / 255.0,
                    colorSecondary.red.toDouble() / 255.0, colorSecondary.green.toDouble() / 255.0, colorSecondary.blue.toDouble() / 255.0, colorSecondary.alpha.toDouble() / 255.0

            )
        }
    }
}
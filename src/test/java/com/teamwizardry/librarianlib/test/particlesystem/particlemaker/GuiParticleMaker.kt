package com.teamwizardry.librarianlib.test.particlesystem.particlemaker

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.provided.pastry.components.*
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryColorPicker
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Align2d
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
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.RandomUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.roundToInt

class GuiParticleMaker : PastryWindow(500, 300) {

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
    var resourceLoc = ResourceLocation("minecraft", "textures/items/clay_ball.png")

    val headerLayer = TextLayer(0, 0, 0, 0)

    override fun layoutChildren() {
        super.layoutChildren()
        headerLayer.pos = vec(0, 0)
        headerLayer.size = header.size
    }

    init {
        enableHeaderControls = false
        PastryTexture.theme = PastryTexture.Theme.DARK
        system.reload()

        headerLayer.text = "Particle Maker"
        headerLayer.align = Align2d.CENTER
        header.add(headerLayer)

        val renderBox = ComponentRect(0, 0, 300 - 20, heighti)
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
                preMouse = mousePos
            }
        }

        renderBox.BUS.hook<GuiComponentEvents.MouseDragEvent> {
            if (renderBoxMouseDown) {
                postMouse = mousePos
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
            GlStateManager.disableLighting()
            // RENDER PARTICLE SYSTEM
        }
        content.componentWrapper().add(renderBox)

        val tabPane = PastryTabPane(500 - 190 - 10, 10, 190, 300 - 20)
        content.componentWrapper().add(tabPane)


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

        val sliderLifetime = makeSlider(
                text = "Set Particle Lifetime",
                y = 10,
                beginValue = lifetime,
                range = 1..500) {
            lifetime = it
        }
        animatableWrapper1.add(sliderLifetime)

        val sliderParticleCount = makeSlider(
                text = "Set Particle Count",
                y = sliderLifetime.yi + 20,
                beginValue = 1,
                range = 1..1000) {
            particleCount = it
        }
        animatableWrapper1.add(sliderParticleCount)

        // --- PARTICLE COUNT --- //


        val aesthetics = tabPane.addTab("Aesthetics")


        // --- RESOURCE LOCATION --- //
        val resourceLocTitle = TextLayer(0, 0, 0, 0)
        resourceLocTitle.fitToText = true
        resourceLocTitle.text = "Resource Location"
        val resourceLocation = ComponentTextField(0, 10, aesthetics.widthi, 12)
        resourceLocation.text = resourceLoc.namespace + ":" + resourceLoc.path
        resourceLocation.BUS.hook<ComponentTextField.TextEditEvent> {
            val whole = it.whole.trim()
            var domain = "minecraft"
            val path: String
            if (whole.contains(":")) {
                val split = whole.split(":")
                domain = split[0]
                path = split[1]
            } else path = whole
            resourceLoc = ResourceLocation(domain, path)
            system.reload()
        }
        aesthetics.add(resourceLocTitle, resourceLocation)
        // --- RESOURCE LOCATION --- //

        val mainRadius = 30.0
        val buttonColorWheelPrimary = PastryButton("Pick Color", aesthetics.widthi / 2 - 90 / 2, resourceLocation.yi + 10 + 25, 110)
        val colorWheelPrimary = PastryColorPicker()
        colorWheelPrimary.BUS.hook<LoseFocusEvent> {
            //       colorWheelPrimary.close()
        }
        colorWheelPrimary.BUS.hook<PastryColorPicker.ColorChangeEvent> {
            colorPrimary = Color.getHSBColor(colorWheelPrimary.hue, colorWheelPrimary.saturation, colorWheelPrimary.brightness)
        }
        buttonColorWheelPrimary.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            colorWheelPrimary.open()
        }

        val buttonColorWheelSecondary = PastryButton("Secondary Color", aesthetics.widthi / 2 - 90 / 2, buttonColorWheelPrimary.yi + 15, 110)
        buttonColorWheelSecondary.isVisible = false
        val colorWheelSecondary = PastryColorPicker()
        colorWheelSecondary.BUS.hook<LoseFocusEvent> {
            //     colorWheelSecondary.close()
        }
        colorWheelSecondary.BUS.hook<PastryColorPicker.ColorChangeEvent> {
            colorSecondary = Color.getHSBColor(colorWheelSecondary.hue, colorWheelSecondary.saturation, colorWheelSecondary.brightness)
        }
        buttonColorWheelSecondary.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            colorWheelSecondary.open()
        }

        aesthetics.add(buttonColorWheelPrimary, buttonColorWheelSecondary)

        // COLOR FADING
        val colorFade = makeSwitch("Enable Color Fading", 0, resourceLocation.yi + 20) {
            colorFading = !it
            if (!it) {
                buttonColorWheelSecondary.isVisible = true
                buttonColorWheelSecondary.size_rm.animate(Vec2d(buttonColorWheelSecondary.width, 0.0), Vec2d(buttonColorWheelSecondary.width, 12.0), 20f, Easing.easeOutQuart)

                buttonColorWheelPrimary.label.text = "Primary Color"
            } else {
                buttonColorWheelPrimary.label.text = "Pick Color"
                buttonColorWheelSecondary.size_rm.animate(Vec2d(buttonColorWheelSecondary.width, 12.0), Vec2d(buttonColorWheelSecondary.width, 0.0), 20f, Easing.easeOutQuart).completion = Runnable {
                    buttonColorWheelSecondary.isVisible = false
                }
            }
        }

        aesthetics.add(colorFade)


        val physics = tabPane.addTab("Physics")


        physics.add(makeSwitch("Enable Collision", 0, 0) { enableCollision = it })


        val animatableWrapper2 = GuiComponent(0, 30)
        physics.add(animatableWrapper2)

        val velXField = ComponentFreeSlider("X", 40, physics.widthi, velX, -50.0..50.0, -1000.0..1000.0) {
            velX = it
        }
        velXField.isVisible = false
        val velYField = ComponentFreeSlider("Y", velXField.yi + velXField.heighti, physics.widthi, velY, -50.0..50.0, -1000.0..1000.0) {
            velY = it
        }
        velYField.isVisible = false
        val velZField = ComponentFreeSlider("Z", velYField.yi + velYField.heighti, physics.widthi, velZ, -50.0..50.0, -1000.0..1000.0) {
            velZ = it
        }
        velZField.isVisible = false
        physics.add(velXField, velYField, velZField)


        val maxVelXField = ComponentFreeSlider("Max X", 40, physics.widthi, maxVelX, -50.0..50.0, -1000.0..1000.0) {
            maxVelX = it
        }
        maxVelXField.isVisible = false
        val maxVelYField = ComponentFreeSlider("Max Y", maxVelXField.yi + maxVelXField.heighti, physics.widthi, maxVelY, -50.0..50.0, -1000.0..1000.0) {
            maxVelY = it
        }
        maxVelYField.isVisible = false
        val maxVelZField = ComponentFreeSlider("Max Z", maxVelYField.yi + maxVelYField.heighti, physics.widthi, maxVelZ, -50.0..50.0, -1000.0..1000.0) {
            maxVelZ = it
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
                    randomVelocity = true
                }
            } else {
                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 60.0), 10f, Easing.easeOutQuart)
                velXField.updateText("X")
                velYField.updateText("Y")
                velZField.updateText("Z")

                maxVelXField.isVisible = false
                maxVelYField.isVisible = false
                maxVelZField.isVisible = false
                randomVelocity = false
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
                }
            } else {
                animatableWrapper2.pos_rm.animate(Vec2d(0.0, 30.0), 10f, Easing.easeOutQuart)
                velXField.isVisible = false
                velYField.isVisible = false
                velZField.isVisible = false
                randomVelocitySwitch.isVisible = false
            }
        })


        animatableWrapper2.add(makeSlider("Gravity", 0, gravity, 0.0..10.0) { gravity = it })
        animatableWrapper2.add(makeSlider("Damping", 20, damping.toDouble(), 0.0..1.0) { damping = it.toFloat() })
        animatableWrapper2.add(makeSlider("Friction", 40, friction.toDouble(), 0.0..1.0) { friction = it.toFloat() })
        animatableWrapper2.add(makeSlider("Bounciness", 60, bounciness.toDouble(), 0.0..1.0) { bounciness = it.toFloat() })
    }

    private fun makeSlider(text: String, y: Int, beginValue: Double, range: ClosedRange<Double>, stateChange: (newProgress: Double) -> Unit): PastrySlider {
        val titleLayer = TextLayer(0, -10, 0, 0)
        titleLayer.fitToText = true
        titleLayer.text = text

        val slider = PastrySlider(0, y + 10, 150, false, Cardinal2d.GUI.DOWN)
        slider.range = range
        slider.value = beginValue
        slider.BUS.fire(PastryToggle.StateChangeEvent(false))

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
        slider.range = range.start.toDouble()..range.endInclusive.toDouble()
        slider.value = beginValue.toDouble()
        slider.BUS.fire(PastryToggle.StateChangeEvent(false))

        val numberLayer = TextLayer(150 + 10, 0, 0, 0)
        numberLayer.fitToText = true
        numberLayer.text = "$beginValue"
        var lastValue = beginValue
        slider.BUS.hook<PastrySlider.ValueChangeEvent> { event ->
            val intValue = event.newValue.roundToInt()
            event.newValue = intValue.toDouble()
            if (intValue != lastValue) {
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
            PhysicsCurtainSystem.spawn(
                lifetime = lifetime.toDouble(),
                size = 16.0, pos = Vec3d(0.0, 10.0, 0.0),

                bounciness = bounciness,
                friction = friction,
                dampness = damping,
                gravity = gravity,
                collision = if (enableCollision) 1 else 0,

                colorFading = if (colorFading) 1 else 0,
                colorPrimary = colorPrimary,
                colorSecondary = colorSecondary,

                velocity = if (velX == maxVelX && velY == maxVelY && velZ == maxVelZ)
                    vec(velX, velY, velZ)
                else if (randomVelocity) {
                    vec(
                        RandomUtils.nextDouble(Math.min(velX, maxVelX), Math.max(velX, maxVelX)),
                        RandomUtils.nextDouble(Math.min(velY, maxVelY), Math.max(velY, maxVelY)),
                        RandomUtils.nextDouble(Math.min(velZ, maxVelZ), Math.max(velZ, maxVelZ)))
                } else {
                    vec(velX, velY, velZ)
                },

                resourceLocation = resourceLoc)
    }

    //   override fun doesGuiPauseGame(): Boolean = false

    object PhysicsCurtainSystem : ParticleSystem() {

        var resourceLocation = ResourceLocation("minecraft", "textures/items/clay_ball.png")

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
                    sprite = resourceLocation,
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
                  colorSecondary: Color = colorPrimary,

                  velocity: Vec3d,

                  resourceLocation: ResourceLocation) {

            PhysicsCurtainSystem.resourceLocation = resourceLocation
            this.addParticle(lifetime,
                    size,
                    pos.x, pos.y, pos.z,
                    pos.x, pos.y, pos.z,
                    velocity.x, velocity.y, velocity.z,

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
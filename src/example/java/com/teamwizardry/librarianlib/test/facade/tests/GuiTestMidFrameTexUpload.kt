package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.layers.RectLayer
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.facade.layout.StackLayout
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Java2DSprite
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
@UseExperimental(ExperimentalBitfont::class)
class GuiTestMidFrameTexUpload : GuiBase() {
    val sprite = Java2DSprite(100, 100)

    init {
        main.size = vec(300, 100)

        val row = StackLayout.build().horizontal().component()

        val before = Column("before")
        val upload = Column("upload")
        val after = Column("after")
//        upload.xi = 100
//        after.xi = 200

        row.add(before, upload, after)
//        main.add(before, upload, after)
        row.width = 300.0
        main.add(row)

        before.body.add(SpriteLayer(sprite))
        after.body.add(SpriteLayer(sprite))

        upload.body.add(RectLayer(Color.RED, 0, 0, 100, 100))
        val box = RectLayer(Color.BLACK, 0, 0, 100, 100)
        upload.body.add(box)

        upload.BUS.hook<GuiLayerEvents.PreDrawEvent> {
            val height = 100 * (ClientTickHandler.ticks % 40.0) / 40.0
            box.height = height
            val g = sprite.begin()
            g.color = Color.RED
            g.fillRect(0, 0, 100, 100)
            g.color = Color.BLACK
            g.fillRect(0, 0, 100, height.toInt())
            sprite.end()
        }
    }

    private class Column(name: String) : GuiComponent(0, 0, 100, 100) {
        private val stack = StackLayout.build().size(100, 100).vertical().component()
        val label = TextLayer(name)
        val body = GuiComponent(100, 100)

        init {
            this.add(stack)
            stack.add(label, body)
            stack.height = 100 + label.height
            this.height = stack.frame.height
        }
    }
}

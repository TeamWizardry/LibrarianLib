package com.teamwizardry.librarianlib.albedo.buffer

import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import net.minecraft.util.Identifier
import java.awt.Color

public object SomethingImportant {
    public fun draw(buffer: RenderBuffer) {}
}

private val shader: Test get() = TODO()
private val buffer = Test.Buffer().also { it.link(shader) }

private fun draw() {
    // wow
    buffer.primitive.set(5)
    buffer.pos(0, 0, 0).color(Color.RED).endVertex()
    buffer.pos(1, 0, 0).color(Color.RED).endVertex()
    buffer.pos(1, 1, 0).color(Color.RED).endVertex()
    buffer.pos(0, 1, 0).color(Color.RED).endVertex()

    // still need to work out how this should work. Who's responsible for keeping track of the VAO?
    SomethingImportant.draw(buffer)
}

private class Test : Shader(
    "my_shader",
    Identifier("some-mod:shaders/my_shader.vert"),
    Identifier("some-mod:shaders/my_shader.frag")
) {
    class Buffer: DefaultBufferTypes.PositionColor() {
        val primitive = +Uniform.int.create("primitive")
    }
}

public interface RenderBufferExtensions {
    public interface ColorExtensions<S> {
        public fun color(red: Int, green: Int, blue: Int, alpha: Int): S
        public fun color(red: Float, green: Float, blue: Float, alpha: Float): S =
            this.color((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

        public fun color(color: Color): S = this.color(color.red, color.green, color.blue, color.alpha)
    }

    public interface PositionExtensions<S> {
        public fun pos(x: Float, y: Float, z: Float): S
        public fun pos(x: Int, y: Int, z: Int): S = this.pos(x.toFloat(), y.toFloat(), z.toFloat())
    }
}

private class DefaultBufferTypes {
    open class PositionColor : RenderBuffer(),
        RenderBufferExtensions.PositionExtensions<PositionColor>,
        RenderBufferExtensions.ColorExtensions<PositionColor> {

        private val _position = +VertexLayoutElement("position", VertexLayoutElement.FloatFormat.FLOAT, 4, false)
        private val _color = +VertexLayoutElement("color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

        override fun pos(x: Float, y: Float, z: Float): PositionColor {
            seek(_position)
            putFloat(x)
            putFloat(y)
            putFloat(z)
            return this
        }

        override fun color(red: Int, green: Int, blue: Int, alpha: Int): PositionColor {
            seek(_color)
            putByte(red)
            putByte(green)
            putByte(blue)
            putByte(alpha)
            return this
        }
    }
}

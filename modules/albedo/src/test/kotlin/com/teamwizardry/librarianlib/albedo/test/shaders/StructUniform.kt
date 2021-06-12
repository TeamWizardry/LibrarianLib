package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.uniform.GLSLStruct
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object StructUniform : ShaderTest<StructUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.simple.primitive.set(1f)
        shader.simple.primitiveArray[0] = 2f
        shader.simple.primitiveArray[1] = 3f
        shader.simple.embedded.embed.set(4f)
        shader.simple.embeddedArray[0].embed.set(5f)
        shader.simple.embeddedArray[1].embed.set(6f)

        shader.simpleArray[0].primitive.set(11f)
        shader.simpleArray[0].primitiveArray[0] = 12f
        shader.simpleArray[0].primitiveArray[1] = 13f
        shader.simpleArray[0].embedded.embed.set(14f)
        shader.simpleArray[0].embeddedArray[0].embed.set(15f)
        shader.simpleArray[0].embeddedArray[1].embed.set(16f)

        shader.simpleArray[1].primitive.set(21f)
        shader.simpleArray[1].primitiveArray[0] = 22f
        shader.simpleArray[1].primitiveArray[1] = 23f
        shader.simpleArray[1].embedded.embed.set(24f)
        shader.simpleArray[1].embeddedArray[0].embed.set(25f)
        shader.simpleArray[1].embeddedArray[1].embed.set(26f)


        drawUnitQuad(matrix)
    }

    class Test : Shader(
        "struct_tests",
        Identifier("liblib-albedo-test:shaders/uniform_base.vert"),
        Identifier("liblib-albedo-test:shaders/struct_tests.frag")
    ) {
        val simple = Uniform.struct.create<Simple>("simple")
        val simpleArray = Uniform.struct.createArray<Simple>("simpleArray", 2)

        class Embedded(name: String) : GLSLStruct(name) {
            val embed = Uniform.float.create("embed")
        }

        class Simple(name: String) : GLSLStruct(name) {
            val primitive = Uniform.float.create("primitive")
            val primitiveArray = Uniform.float.createArray("primitiveArray", 2)
            val embedded = Uniform.struct.create<Embedded>("embedded")
            val embeddedArray = Uniform.struct.createArray<Embedded>("embeddedArray", 2)
        }
    }
}
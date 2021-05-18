package com.teamwizardry.librarianlib.albedo.test

import com.teamwizardry.librarianlib.albedo.Shader
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror
import net.minecraft.client.util.math.MatrixStack

abstract class ShaderTest<T: Shader> {
    protected abstract fun doDraw(matrixStack: MatrixStack)

    private var _shader: Shader? = null
    @Suppress("UNCHECKED_CAST")
    protected val shader: T
        get() = _shader!! as T

    private val shaderConstructor: ConstructorMirror = Mirror.reflectClass(this.javaClass)
        .findSuperclass(ShaderTest::class.java)!!
        .typeParameters[0].asClassMirror()
        .getDeclaredConstructor()

    fun draw(matrixStack: MatrixStack) {
        if(_shader == null) {
            _shader = shaderConstructor()
        }
        doDraw(matrixStack)
    }

    fun delete() {
        _shader?.delete()
        _shader = null
    }
}
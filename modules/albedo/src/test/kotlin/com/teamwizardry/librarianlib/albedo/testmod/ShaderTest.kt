package com.teamwizardry.librarianlib.albedo.testmod

import com.teamwizardry.librarianlib.albedo.Shader
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.member.ConstructorMirror

abstract class ShaderTest<T: Shader> {
    protected abstract fun doDraw()

    private var _shader: Shader? = null
    @Suppress("UNCHECKED_CAST")
    protected val shader: T
        get() = _shader!! as T

    private val shaderConstructor: ConstructorMirror = Mirror.reflectClass(this.javaClass)
        .findSuperclass(ShaderTest::class.java)!!
        .typeParameters[0].asClassMirror()
        .getDeclaredConstructor()

    fun draw() {
        if(_shader == null) {
            _shader = shaderConstructor()
        }
        doDraw()
    }

    fun delete() {
        _shader?.delete()
        _shader = null
    }
}
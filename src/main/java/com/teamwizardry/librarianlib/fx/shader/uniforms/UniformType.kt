package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.fx.shader.Shader
import com.teamwizardry.librarianlib.fx.shader.uniforms.Uniform.NoUniform
import org.lwjgl.opengl.*
import sun.jvm.hotspot.debugger.cdbg.FloatType
import java.util.*

enum class UniformType constructor(private val initializer: (Shader, String, UniformType, Int, Int) -> Uniform) {
    NONE(::NoUniform),
    // bools
    BOOL({ owner, name, type, size, location -> BoolTypes.Bool1(owner, name, type, size, location)}),
    BOOL_VEC2({ owner, name, type, size, location -> BoolTypes.BoolVec2(owner, name, type, size, location)}),
    BOOL_VEC3({ owner, name, type, size, location -> BoolTypes.BoolVec3(owner, name, type, size, location)}),
    BOOL_VEC4({ owner, name, type, size, location -> BoolTypes.BoolVec4(owner, name, type, size, location)}),

    // ints
    INT({ owner, name, type, size, location -> IntTypes.Int1(owner, name, type, size, location)}),
    INT_VEC2({ owner, name, type, size, location -> IntTypes.IntVec2(owner, name, type, size, location)}),
    INT_VEC3({ owner, name, type, size, location -> IntTypes.IntVec3(owner, name, type, size, location)}),
    INT_VEC4({ owner, name, type, size, location -> IntTypes.IntVec4(owner, name, type, size, location)}),

    // unsigned ints
    UINT(::NoUniform),
    UINT_VEC2(::NoUniform),
    UINT_VEC3(::NoUniform),
    UINT_VEC4(::NoUniform),
    UINT_ATOMIC_COUNTER(::NoUniform),

    // floats
    FLOAT({ owner, name, type, size, location -> FloatTypes.Float1(owner, name, type, size, location)}),
    FLOAT_VEC2({ owner, name, type, size, location -> FloatTypes.FloatVec2(owner, name, type, size, location)}),
    FLOAT_VEC3({ owner, name, type, size, location -> FloatTypes.FloatVec3(owner, name, type, size, location)}),
    FLOAT_VEC4({ owner, name, type, size, location -> FloatTypes.FloatVec4(owner, name, type, size, location)}),

    FLOAT_MAT2(::NoUniform),
    FLOAT_MAT3(::NoUniform),
    FLOAT_MAT4(::NoUniform),
    FLOAT_MAT2x3(::NoUniform),
    FLOAT_MAT2x4(::NoUniform),
    FLOAT_MAT3x2(::NoUniform),
    FLOAT_MAT3x4(::NoUniform),
    FLOAT_MAT4x2(::NoUniform),
    FLOAT_MAT4x3(::NoUniform),

    // doubles
    DOUBLE(::NoUniform),
    DOUBLE_VEC2(::NoUniform),
    DOUBLE_VEC3(::NoUniform),
    DOUBLE_VEC4(::NoUniform),

    DOUBLE_MAT2(::NoUniform),
    DOUBLE_MAT3(::NoUniform),
    DOUBLE_MAT4(::NoUniform),
    DOUBLE_MAT2x3(::NoUniform),
    DOUBLE_MAT2x4(::NoUniform),
    DOUBLE_MAT3x2(::NoUniform),
    DOUBLE_MAT3x4(::NoUniform),
    DOUBLE_MAT4x2(::NoUniform),
    DOUBLE_MAT4x3(::NoUniform),

    // samplers: 1D, 2D, other
    SAMPLER_1D(::NoUniform),
    SAMPLER_1D_SHADOW(::NoUniform),
    SAMPLER_1D_ARRAY(::NoUniform),
    SAMPLER_1D_ARRAY_SHADOW(::NoUniform),

    SAMPLER_2D(::NoUniform),
    SAMPLER_2D_SHADOW(::NoUniform),
    SAMPLER_2D_ARRAY(::NoUniform),
    SAMPLER_2D_ARRAY_SHADOW(::NoUniform),
    SAMPLER_2D_MULTISAMPLE(::NoUniform),
    SAMPLER_2D_MULTISAMPLE_ARRAY(::NoUniform),

    SAMPLER_3D(::NoUniform),
    SAMPLER_CUBE(::NoUniform),
    SAMPLER_CUBE_SHADOW(::NoUniform),
    SAMPLER_BUFFER(::NoUniform),
    SAMPLER_2D_RECT(::NoUniform),
    SAMPLER_2D_RECT_SHADOW(::NoUniform),

    // int samplers: 1D, 2D, other
    INT_SAMPLER_1D(::NoUniform),
    INT_SAMPLER_1D_ARRAY(::NoUniform),

    INT_SAMPLER_2D(::NoUniform),
    INT_SAMPLER_2D_ARRAY(::NoUniform),
    INT_SAMPLER_2D_MULTISAMPLE(::NoUniform),
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY(::NoUniform),

    INT_SAMPLER_3D(::NoUniform),
    INT_SAMPLER_CUBE(::NoUniform),
    INT_SAMPLER_BUFFER(::NoUniform),
    INT_SAMPLER_2D_RECT(::NoUniform),

    // unsigned int samplers: 1D, 2D, other
    UINT_SAMPLER_1D(::NoUniform),
    UINT_SAMPLER_1D_ARRAY(::NoUniform),

    UINT_SAMPLER_2D(::NoUniform),
    UINT_SAMPLER_2D_ARRAY(::NoUniform),
    UINT_SAMPLER_2D_MULTISAMPLE(::NoUniform),
    UINT_SAMPLER_2D_MULTISAMPLE_ARRAY(::NoUniform),

    UINT_SAMPLER_3D(::NoUniform),
    UINT_SAMPLER_CUBE(::NoUniform),
    UINT_SAMPLER_BUFFER(::NoUniform),
    UINT_SAMPLER_2D_RECT(::NoUniform),

    // images: 1D, 2D, other
    IMAGE_1D(::NoUniform),
    IMAGE_1D_ARRAY(::NoUniform),

    IMAGE_2D(::NoUniform),
    IMAGE_2D_ARRAY(::NoUniform),
    IMAGE_2D_RECT(::NoUniform),
    IMAGE_2D_MULTISAMPLE(::NoUniform),
    IMAGE_2D_MULTISAMPLE_ARRAY(::NoUniform),

    IMAGE_3D(::NoUniform),
    IMAGE_CUBE(::NoUniform),
    IMAGE_BUFFER(::NoUniform),


    // int images: 1D, 2D, other
    INT_IMAGE_1D(::NoUniform),
    INT_IMAGE_1D_ARRAY(::NoUniform),

    INT_IMAGE_2D(::NoUniform),
    INT_IMAGE_2D_ARRAY(::NoUniform),
    INT_IMAGE_2D_RECT(::NoUniform),
    INT_IMAGE_2D_MULTISAMPLE(::NoUniform),
    INT_IMAGE_2D_MULTISAMPLE_ARRAY(::NoUniform),

    INT_IMAGE_3D(::NoUniform),
    INT_IMAGE_CUBE(::NoUniform),
    INT_IMAGE_BUFFER(::NoUniform),

    // unsigned int images: 1D, 2D, other
    UINT_IMAGE_1D(::NoUniform),
    UINT_IMAGE_1D_ARRAY(::NoUniform),

    UINT_IMAGE_2D(::NoUniform),
    UINT_IMAGE_2D_ARRAY(::NoUniform),
    UINT_IMAGE_2D_RECT(::NoUniform),
    UINT_IMAGE_2D_MULTISAMPLE(::NoUniform),
    UINT_IMAGE_2D_MULTISAMPLE_ARRAY(::NoUniform),

    UINT_IMAGE_3D(::NoUniform),
    UINT_IMAGE_CUBE(::NoUniform),
    UINT_IMAGE_BUFFER(::NoUniform);

    protected var type: Int = 0

    fun make(owner: Shader, name: String, type: UniformType, size: Int, location: Int): Uniform {
        return initializer(owner, name, type, size, location)
    }

    companion object {

        internal var map: MutableMap<Int, UniformType> = HashMap()
        internal var classes = arrayOf<Class<*>>(GL11::class.java, GL20::class.java, GL21::class.java, GL30::class.java, GL31::class.java, GL32::class.java, GL33::class.java, GL40::class.java, GL42::class.java)

        init {
            // advanced shader -> gl21, basic shader -> gl20;
            for (type in values()) {
                val name = "GL_" + type.name.replace("UINT".toRegex(), "UNSIGNED_INT")
                for (clazz in classes) {
                    try {
                        val f = clazz.getField(name)
                        val t = f.type
                        if (t == Integer.TYPE) {
                            type.type = f.getInt(null)
                            map.put(type.type, type)
                            //LibrarianLog.I.debug(" == Found %s.%s, it is %d (0x%s)", clazz.getName(), name, type.type, Integer.toHexString(type.type));
                            break
                        }
                    } catch (e: NoSuchFieldException) {
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalAccessException) {
                    }

                }
                if (!map.containsValue(type)) {
                    LibrarianLog.error("Couldn't find uniform OpenGL constant for %s", name)
                }
            }
        }

        fun getByGlEnum(type: Int): UniformType {
            var uniformType: UniformType? = map[type]
            if (uniformType == null)
                uniformType = NONE
            return uniformType
        }
    }

}

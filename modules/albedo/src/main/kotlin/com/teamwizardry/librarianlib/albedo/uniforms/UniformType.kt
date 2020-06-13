package com.teamwizardry.librarianlib.albedo.uniforms

import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.uniforms.Uniform.NoUniform
import com.teamwizardry.librarianlib.albedo.uniforms.Uniform.UnsupportedUniform
import org.lwjgl.opengl.*
import java.util.*

@Suppress("EnumEntryName", "unused")
enum class UniformType constructor(private val initializer: (location: Int) -> Uniform<*>) {
    NONE(::NoUniform),
    // bools
    BOOL(BoolTypes::BoolUniform),
    BOOL_VEC2(BoolTypes::BoolVec2Uniform),
    BOOL_VEC3(BoolTypes::BoolVec3Uniform),
    BOOL_VEC4(BoolTypes::BoolVec4Uniform),

    // ints
    INT(IntTypes::IntUniform),
    INT_VEC2(IntTypes::IntVec2Uniform),
    INT_VEC3(IntTypes::IntVec3Uniform),
    INT_VEC4(IntTypes::IntVec4Uniform),

    // floats
    FLOAT(FloatTypes::FloatUniform),
    FLOAT_VEC2(FloatTypes::FloatVec2Uniform),
    FLOAT_VEC3(FloatTypes::FloatVec3Uniform),
    FLOAT_VEC4(FloatTypes::FloatVec4Uniform),

    FLOAT_MAT2(::NoUniform),
    FLOAT_MAT3(::NoUniform),
    FLOAT_MAT4(::NoUniform),
    FLOAT_MAT2x3(::NoUniform),
    FLOAT_MAT2x4(::NoUniform),
    FLOAT_MAT3x2(::NoUniform),
    FLOAT_MAT3x4(::NoUniform),
    FLOAT_MAT4x2(::NoUniform),
    FLOAT_MAT4x3(::NoUniform),

    SAMPLER_1D(::NoUniform),
    SAMPLER_1D_SHADOW(::NoUniform),
    SAMPLER_2D(::NoUniform),
    SAMPLER_2D_SHADOW(::NoUniform),
    SAMPLER_3D(::NoUniform),
    SAMPLER_CUBE(::NoUniform),

    //region GLSL 1.30 (tentative support)
    UINT(::UnsupportedUniform),
    UINT_VEC2(::UnsupportedUniform),
    UINT_VEC3(::UnsupportedUniform),
    UINT_VEC4(::UnsupportedUniform),

    SAMPLER_1D_ARRAY(::UnsupportedUniform),
    SAMPLER_1D_ARRAY_SHADOW(::UnsupportedUniform),

    SAMPLER_2D_ARRAY(::UnsupportedUniform),
    SAMPLER_2D_ARRAY_SHADOW(::UnsupportedUniform),


    INT_SAMPLER_1D(::UnsupportedUniform),
    INT_SAMPLER_1D_ARRAY(::UnsupportedUniform),
    INT_SAMPLER_2D(::UnsupportedUniform),
    INT_SAMPLER_2D_ARRAY(::UnsupportedUniform),
    INT_SAMPLER_3D(::UnsupportedUniform),
    INT_SAMPLER_CUBE(::UnsupportedUniform),

    UINT_SAMPLER_1D(::UnsupportedUniform),
    UINT_SAMPLER_1D_ARRAY(::UnsupportedUniform),
    UINT_SAMPLER_2D(::UnsupportedUniform),
    UINT_SAMPLER_2D_ARRAY(::UnsupportedUniform),
    UINT_SAMPLER_3D(::UnsupportedUniform),
    UINT_SAMPLER_CUBE(::UnsupportedUniform),
    //endregion

    //region GLSL 1.40+ (we only support up to 1.30) ===================================================================
    UINT_ATOMIC_COUNTER(::UnsupportedUniform),

    DOUBLE(::UnsupportedUniform),
    DOUBLE_VEC2(::UnsupportedUniform),
    DOUBLE_VEC3(::UnsupportedUniform),
    DOUBLE_VEC4(::UnsupportedUniform),

    DOUBLE_MAT2(::UnsupportedUniform),
    DOUBLE_MAT3(::UnsupportedUniform),
    DOUBLE_MAT4(::UnsupportedUniform),
    DOUBLE_MAT2x3(::UnsupportedUniform),
    DOUBLE_MAT2x4(::UnsupportedUniform),
    DOUBLE_MAT3x2(::UnsupportedUniform),
    DOUBLE_MAT3x4(::UnsupportedUniform),
    DOUBLE_MAT4x2(::UnsupportedUniform),
    DOUBLE_MAT4x3(::UnsupportedUniform),

    SAMPLER_2D_MULTISAMPLE(::UnsupportedUniform),
    SAMPLER_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),

    SAMPLER_CUBE_SHADOW(::UnsupportedUniform),
    SAMPLER_BUFFER(::UnsupportedUniform),
    SAMPLER_2D_RECT(::UnsupportedUniform),
    SAMPLER_2D_RECT_SHADOW(::UnsupportedUniform),

    INT_SAMPLER_2D_MULTISAMPLE(::UnsupportedUniform),
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),
    INT_SAMPLER_BUFFER(::UnsupportedUniform),
    INT_SAMPLER_2D_RECT(::UnsupportedUniform),

    UINT_SAMPLER_2D_MULTISAMPLE(::UnsupportedUniform),
    UINT_SAMPLER_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),
    UINT_SAMPLER_BUFFER(::UnsupportedUniform),
    UINT_SAMPLER_2D_RECT(::UnsupportedUniform),

    IMAGE_1D(::UnsupportedUniform),
    IMAGE_1D_ARRAY(::UnsupportedUniform),

    IMAGE_2D(::UnsupportedUniform),
    IMAGE_2D_ARRAY(::UnsupportedUniform),
    IMAGE_2D_RECT(::UnsupportedUniform),
    IMAGE_2D_MULTISAMPLE(::UnsupportedUniform),
    IMAGE_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),

    IMAGE_3D(::UnsupportedUniform),
    IMAGE_CUBE(::UnsupportedUniform),
    IMAGE_BUFFER(::UnsupportedUniform),

    INT_IMAGE_1D(::UnsupportedUniform),
    INT_IMAGE_1D_ARRAY(::UnsupportedUniform),

    INT_IMAGE_2D(::UnsupportedUniform),
    INT_IMAGE_2D_ARRAY(::UnsupportedUniform),
    INT_IMAGE_2D_RECT(::UnsupportedUniform),
    INT_IMAGE_2D_MULTISAMPLE(::UnsupportedUniform),
    INT_IMAGE_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),

    INT_IMAGE_3D(::UnsupportedUniform),
    INT_IMAGE_CUBE(::UnsupportedUniform),
    INT_IMAGE_BUFFER(::UnsupportedUniform),

    UINT_IMAGE_1D(::UnsupportedUniform),
    UINT_IMAGE_1D_ARRAY(::UnsupportedUniform),

    UINT_IMAGE_2D(::UnsupportedUniform),
    UINT_IMAGE_2D_ARRAY(::UnsupportedUniform),
    UINT_IMAGE_2D_RECT(::UnsupportedUniform),
    UINT_IMAGE_2D_MULTISAMPLE(::UnsupportedUniform),
    UINT_IMAGE_2D_MULTISAMPLE_ARRAY(::UnsupportedUniform),

    UINT_IMAGE_3D(::UnsupportedUniform),
    UINT_IMAGE_CUBE(::UnsupportedUniform),
    UINT_IMAGE_BUFFER(::UnsupportedUniform);

    //endregion

    protected var type: Int = 0

    fun make(owner: Shader, name: String, type: UniformType, size: Int, location: Int): Uniform {
        return initializer(owner, name, type, size, location)
    }

    companion object {

        private var map: MutableMap<Int, UniformType> = HashMap()
        private var classes = arrayOf<Class<*>>(GL11::class.java, GL20::class.java, GL21::class.java, GL30::class.java, GL31::class.java, GL32::class.java, GL33::class.java, GL40::class.java, GL42::class.java)

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
                    } catch (e: Throwable) {
                        //NO-OP
                    }

                }
                if (!map.containsValue(type))
                    LibrarianLog.error("Couldn't find uniform OpenGL constant for %s", name)
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

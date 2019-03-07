package com.teamwizardry.librarianlib.features.shader.uniforms

import com.teamwizardry.librarianlib.core.LibrarianLog
import org.lwjgl.opengl.*
import java.util.*

enum class UniformType{
    NONE,
    // bools
    BOOL,
    BOOL_VEC2,
    BOOL_VEC3,
    BOOL_VEC4,

    // ints
    INT,
    INT_VEC2,
    INT_VEC3,
    INT_VEC4,

    // unsigned ints
    UINT,
    UINT_VEC2,
    UINT_VEC3,
    UINT_VEC4,
    UINT_ATOMIC_COUNTER,

    // floats
    FLOAT,
    FLOAT_VEC2,
    FLOAT_VEC3,
    FLOAT_VEC4,

    FLOAT_MAT2,
    FLOAT_MAT3,
    FLOAT_MAT4,
    FLOAT_MAT2x3,
    FLOAT_MAT2x4,
    FLOAT_MAT3x2,
    FLOAT_MAT3x4,
    FLOAT_MAT4x2,
    FLOAT_MAT4x3,

    // doubles
    DOUBLE,
    DOUBLE_VEC2,
    DOUBLE_VEC3,
    DOUBLE_VEC4,

    DOUBLE_MAT2,
    DOUBLE_MAT3,
    DOUBLE_MAT4,
    DOUBLE_MAT2x3,
    DOUBLE_MAT2x4,
    DOUBLE_MAT3x2,
    DOUBLE_MAT3x4,
    DOUBLE_MAT4x2,
    DOUBLE_MAT4x3,

    // samplers: 1D, 2D, other
    SAMPLER_1D,
    SAMPLER_1D_SHADOW,
    SAMPLER_1D_ARRAY,
    SAMPLER_1D_ARRAY_SHADOW,

    SAMPLER_2D,
    SAMPLER_2D_SHADOW,
    SAMPLER_2D_ARRAY,
    SAMPLER_2D_ARRAY_SHADOW,
    SAMPLER_2D_MULTISAMPLE,
    SAMPLER_2D_MULTISAMPLE_ARRAY,

    SAMPLER_3D,
    SAMPLER_CUBE,
    SAMPLER_CUBE_SHADOW,
    SAMPLER_BUFFER,
    SAMPLER_2D_RECT,
    SAMPLER_2D_RECT_SHADOW,

    // int samplers: 1D, 2D, other
    INT_SAMPLER_1D,
    INT_SAMPLER_1D_ARRAY,

    INT_SAMPLER_2D,
    INT_SAMPLER_2D_ARRAY,
    INT_SAMPLER_2D_MULTISAMPLE,
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY,

    INT_SAMPLER_3D,
    INT_SAMPLER_CUBE,
    INT_SAMPLER_BUFFER,
    INT_SAMPLER_2D_RECT,

    // unsigned int samplers: 1D, 2D, other
    UINT_SAMPLER_1D,
    UINT_SAMPLER_1D_ARRAY,

    UINT_SAMPLER_2D,
    UINT_SAMPLER_2D_ARRAY,
    UINT_SAMPLER_2D_MULTISAMPLE,
    UINT_SAMPLER_2D_MULTISAMPLE_ARRAY,

    UINT_SAMPLER_3D,
    UINT_SAMPLER_CUBE,
    UINT_SAMPLER_BUFFER,
    UINT_SAMPLER_2D_RECT,

    // images: 1D, 2D, other
    IMAGE_1D,
    IMAGE_1D_ARRAY,

    IMAGE_2D,
    IMAGE_2D_ARRAY,
    IMAGE_2D_RECT,
    IMAGE_2D_MULTISAMPLE,
    IMAGE_2D_MULTISAMPLE_ARRAY,

    IMAGE_3D,
    IMAGE_CUBE,
    IMAGE_BUFFER,


    // int images: 1D, 2D, other
    INT_IMAGE_1D,
    INT_IMAGE_1D_ARRAY,

    INT_IMAGE_2D,
    INT_IMAGE_2D_ARRAY,
    INT_IMAGE_2D_RECT,
    INT_IMAGE_2D_MULTISAMPLE,
    INT_IMAGE_2D_MULTISAMPLE_ARRAY,

    INT_IMAGE_3D,
    INT_IMAGE_CUBE,
    INT_IMAGE_BUFFER,

    // unsigned int images: 1D, 2D, other
    UINT_IMAGE_1D,
    UINT_IMAGE_1D_ARRAY,

    UINT_IMAGE_2D,
    UINT_IMAGE_2D_ARRAY,
    UINT_IMAGE_2D_RECT,
    UINT_IMAGE_2D_MULTISAMPLE,
    UINT_IMAGE_2D_MULTISAMPLE_ARRAY,

    UINT_IMAGE_3D,
    UINT_IMAGE_CUBE,
    UINT_IMAGE_BUFFER;

    var constant: Int = 0
        private set

    companion object {

        internal var map: MutableMap<Int, UniformType> = HashMap()
        internal var classes = arrayOf<Class<*>>(GL11::class.java, GL20::class.java, GL21::class.java, GL30::class.java, GL31::class.java, GL32::class.java, GL33::class.java, GL40::class.java, GL42::class.java)

        init {
            // advanced shader -> gl21, basic shader -> gl20;
            for (type in values()) {
                val constant = "GL_" + type.name.replace("UINT".toRegex(), "UNSIGNED_INT")
                for (clazz in classes) {
                    try {
                        val f = clazz.getField(constant)
                        val t = f.type
                        if (t == Integer.TYPE) {
                            type.constant = f.getInt(null)
                            map[type.constant] = type
                            //LibrarianLog.I.debug(" == Found %s.%s, it is %d (0x%s)", clazz.getName(), name, type.type, Integer.toHexString(type.type));
                            break
                        }
                    } catch (e: Throwable) {
                        //NO-OP
                    }

                }
                if (!map.containsValue(type))
                    LibrarianLog.error("Couldn't find uniform OpenGL constant for %s", constant)
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

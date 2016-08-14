package com.teamwizardry.librarianlib.fx.shader.uniforms

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.fx.shader.Shader
import com.teamwizardry.librarianlib.fx.shader.uniforms.Uniform.NoUniform
import org.lwjgl.opengl.*

import java.lang.reflect.Field
import java.util.HashMap

enum class UniformType private constructor(private val initializer: UniformType.UniformInitializer) {
    NONE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    // bools
    BOOL(UniformInitializer { owner, name, type, size, location -> BoolTypes.Bool(owner, name, type, size, location) }),
    BOOL_VEC2(UniformInitializer { owner, name, type, size, location -> BoolTypes.BoolVec2(owner, name, type, size, location) }),
    BOOL_VEC3(UniformInitializer { owner, name, type, size, location -> BoolTypes.BoolVec3(owner, name, type, size, location) }),
    BOOL_VEC4(UniformInitializer { owner, name, type, size, location -> BoolTypes.BoolVec4(owner, name, type, size, location) }),

    // ints
    INT(UniformInitializer { owner, name, type, size, location -> IntTypes.Int(owner, name, type, size, location) }),
    INT_VEC2(UniformInitializer { owner, name, type, size, location -> IntTypes.IntVec2(owner, name, type, size, location) }),
    INT_VEC3(UniformInitializer { owner, name, type, size, location -> IntTypes.IntVec3(owner, name, type, size, location) }),
    INT_VEC4(UniformInitializer { owner, name, type, size, location -> IntTypes.IntVec4(owner, name, type, size, location) }),

    // unsigned ints
    UINT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_VEC2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_VEC3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_VEC4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_ATOMIC_COUNTER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // floats
    FLOAT(UniformInitializer { owner, name, type, size, location -> FloatTypes.Float(owner, name, type, size, location) }),
    FLOAT_VEC2(UniformInitializer { owner, name, type, size, location -> FloatTypes.FloatVec2(owner, name, type, size, location) }),
    FLOAT_VEC3(UniformInitializer { owner, name, type, size, location -> FloatTypes.FloatVec3(owner, name, type, size, location) }),
    FLOAT_VEC4(UniformInitializer { owner, name, type, size, location -> FloatTypes.FloatVec4(owner, name, type, size, location) }),

    FLOAT_MAT2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT2x3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT2x4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT3x2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT3x4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT4x2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    FLOAT_MAT4x3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // doubles
    DOUBLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_VEC2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_VEC3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_VEC4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    DOUBLE_MAT2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT2x3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT2x4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT3x2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT3x4(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT4x2(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    DOUBLE_MAT4x3(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // samplers: 1D, 2D, other
    SAMPLER_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_1D_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_1D_ARRAY_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    SAMPLER_2D(UniformInitializer { owner, name, type, size, location -> IntTypes.Int(owner, name, type, size, location) }),
    SAMPLER_2D_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_ARRAY_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    SAMPLER_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_CUBE_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    SAMPLER_2D_RECT_SHADOW(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // int samplers: 1D, 2D, other
    INT_SAMPLER_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    INT_SAMPLER_2D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    INT_SAMPLER_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_SAMPLER_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // unsigned int samplers: 1D, 2D, other
    UINT_SAMPLER_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    UINT_SAMPLER_2D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    UINT_SAMPLER_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_SAMPLER_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // images: 1D, 2D, other
    IMAGE_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    IMAGE_2D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    IMAGE_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    IMAGE_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),


    // int images: 1D, 2D, other
    INT_IMAGE_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    INT_IMAGE_2D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    INT_IMAGE_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    INT_IMAGE_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    // unsigned int images: 1D, 2D, other
    UINT_IMAGE_1D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_1D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    UINT_IMAGE_2D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_2D_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_2D_RECT(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_2D_MULTISAMPLE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_2D_MULTISAMPLE_ARRAY(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),

    UINT_IMAGE_3D(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_CUBE(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) }),
    UINT_IMAGE_BUFFER(UniformInitializer { owner, name, type, size, location -> NoUniform(owner, name, type, size, location) });

    protected var type: Int = 0

    fun make(owner: Shader, name: String, type: UniformType, size: Int, location: Int): Uniform {
        return initializer.make(owner, name, type, size, location)
    }

    @FunctionalInterface
    interface UniformInitializer {
        fun make(owner: Shader, name: String, type: UniformType, size: Int, location: Int): Uniform
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
                    LibrarianLog.I.error("Couldn't find uniform OpenGL constant for %s", name)
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

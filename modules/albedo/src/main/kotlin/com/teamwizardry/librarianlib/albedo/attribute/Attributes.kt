package com.teamwizardry.librarianlib.albedo.attribute

import org.lwjgl.opengl.GL41.*
import java.lang.IllegalArgumentException

public class VertexLayoutElement private constructor(
    public val name: String,
    public val format: AttributeFormat,
    public val components: Int,
    public val normalized: Boolean
) {
    public constructor(
        name: String,
        format: FloatFormat,
        components: Int,
        normalized: Boolean
    ) : this(name, format as AttributeFormat, components, normalized)

    public constructor(
        name: String,
        format: IntFormat,
        components: Int
    ) : this(name, format as AttributeFormat, components, false)

    public constructor(
        name: String,
        format: DoubleFormat,
        components: Int
    ) : this(name, format as AttributeFormat, components, false)

    public var index: Int = -1
        @JvmSynthetic internal set
    public var offset: Int = 0
        @JvmSynthetic internal set

    public val width: Int = format.width * components

    init {
        if ((format == FloatFormat.INT_2_10_10_10_REV || format == FloatFormat.UNSIGNED_INT_2_10_10_10_REV) && components != 4) {
            throw IllegalArgumentException("Format $format requires exactly four components, not $components")
        }
    }

    public fun setupVertexAttribPointer(stride: Int) {
        if (index == -1)
            return
        glEnableVertexAttribArray(index)
        when (format) {
            is FloatFormat -> glVertexAttribPointer(
                index,
                components, format.glType, normalized,
                stride, offset.toLong()
            )
            is IntFormat -> glVertexAttribIPointer(
                index,
                components, format.glType,
                stride, offset.toLong()
            )
            is DoubleFormat -> glVertexAttribLPointer(
                index,
                components, format.glType,
                stride, offset.toLong()
            )
        }
    }

    public interface AttributeFormat {
        public val glType: Int
        public val width: Int
    }

    public enum class FloatFormat(override val glType: Int, override val width: Int) : AttributeFormat {
        HALF_FLOAT(GL_HALF_FLOAT, 2), FLOAT(GL_FLOAT, 4), DOUBLE(GL_DOUBLE, 8), FIXED(GL_FIXED, 4),
        BYTE(GL_BYTE, 1), UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1),
        SHORT(GL_SHORT, 2), UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2),
        INT(GL_INT, 4), UNSIGNED_INT(GL_UNSIGNED_INT, 4),
        INT_2_10_10_10_REV(GL_INT_2_10_10_10_REV, 1), // these are both four bytes, but require four components
        UNSIGNED_INT_2_10_10_10_REV(GL_UNSIGNED_INT_2_10_10_10_REV, 1),
        // UNSIGNED_INT_10F_11F_11F_REV(GL_UNSIGNED_INT_10F_11F_11F_REV, 4), // OpenGL 4.4 isn't supported on macOS
    }

    public enum class IntFormat(override val glType: Int, override val width: Int) : AttributeFormat {
        BYTE(GL_BYTE, 1), UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1),
        SHORT(GL_SHORT, 2), UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2),
        INT(GL_INT, 4), UNSIGNED_INT(GL_UNSIGNED_INT, 4),
    }

    public enum class DoubleFormat(override val glType: Int, override val width: Int) : AttributeFormat {
        DOUBLE(GL_DOUBLE, 8)
    }
}

public class VertexAttribute(public val name: String) {
    public var index: Int = -1
        @JvmSynthetic internal set
    public var offset: Int = -1
}

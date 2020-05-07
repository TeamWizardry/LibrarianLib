package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.NBTBuilder
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.MutableMatrix3d
import com.teamwizardry.librarianlib.math.MutableMatrix4d
import com.teamwizardry.librarianlib.math.Quaternion
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.Vec2i
import com.teamwizardry.librarianlib.prism.nbt.Matrix3dSerializer
import com.teamwizardry.librarianlib.prism.nbt.Matrix4dSerializer
import com.teamwizardry.librarianlib.prism.nbt.MutableMatrix3dSerializer
import com.teamwizardry.librarianlib.prism.nbt.MutableMatrix4dSerializer
import com.teamwizardry.librarianlib.prism.nbt.QuaternionSerializer
import com.teamwizardry.librarianlib.prism.nbt.Rect2dSerializer
import com.teamwizardry.librarianlib.prism.nbt.Vec2dSerializer
import com.teamwizardry.librarianlib.prism.nbt.Vec2iSerializer
import dev.thecodewarrior.mirror.Mirror
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

internal class LibrarianLibSimpleTests: NBTPrismTest() {
    @Test
    fun `read+write for Vec2d should be symmetrical`() {
        simple<Vec2d, Vec2dSerializer>(Vec2d(1.0, 2.0), NBTBuilder.compound {
            "X" *= double(1.0)
            "Y" *= double(2.0)
        })
    }

    @Test
    fun `read+write for Vec2i should be symmetrical`() {
        simple<Vec2i, Vec2iSerializer>(Vec2i(1, 2), NBTBuilder.compound {
            "X" *= int(1)
            "Y" *= int(2)
        })
    }

    @Test
    fun `read+write for Rect2d should be symmetrical`() {
        simple<Rect2d, Rect2dSerializer>(Rect2d(1.0, 2.0, 3.0, 4.0), NBTBuilder.compound {
            "X" *= double(1.0)
            "Y" *= double(2.0)
            "Width" *= double(3.0)
            "Height" *= double(4.0)
        })
    }

    @Test
    fun `read+write for Matrix3d should be symmetrical`() {
        simple<Matrix3d, Matrix3dSerializer>(Matrix3d(
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0,
            7.0, 8.0, 9.0
        ), NBTBuilder.list {
            n+ double(1.0)
            n+ double(2.0)
            n+ double(3.0)
            n+ double(4.0)
            n+ double(5.0)
            n+ double(6.0)
            n+ double(7.0)
            n+ double(8.0)
            n+ double(9.0)
        })
    }

    @Test
    fun `read+write for MutableMatrix3d should be symmetrical`() {
        simple<MutableMatrix3d, MutableMatrix3dSerializer>(MutableMatrix3d(
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0,
            7.0, 8.0, 9.0
        ), NBTBuilder.list {
            n+ double(1.0)
            n+ double(2.0)
            n+ double(3.0)
            n+ double(4.0)
            n+ double(5.0)
            n+ double(6.0)
            n+ double(7.0)
            n+ double(8.0)
            n+ double(9.0)
        })
    }

    @Test
    fun `read+write for MutableMatrix3d should modify the existing value`() {
        val goal = MutableMatrix3d(
            1.0, 2.0, 3.0,
            4.0, 5.0, 6.0,
            7.0, 8.0, 9.0
        )
        val target = MutableMatrix3d()
        val result = prism[Mirror.reflect<MutableMatrix3d>()].value.read(
            NBTBuilder.list {
                n+ double(1.0)
                n+ double(2.0)
                n+ double(3.0)
                n+ double(4.0)
                n+ double(5.0)
                n+ double(6.0)
                n+ double(7.0)
                n+ double(8.0)
                n+ double(9.0)
            },
            target
        )
        assertSame(target, result)
        assertEquals(goal, target)
    }

    @Test
    fun `read+write for Matrix4d should be symmetrical`() {
        simple<Matrix4d, Matrix4dSerializer>(Matrix4d(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        ), NBTBuilder.list {
            n+ double(1.0)
            n+ double(2.0)
            n+ double(3.0)
            n+ double(4.0)
            n+ double(5.0)
            n+ double(6.0)
            n+ double(7.0)
            n+ double(8.0)
            n+ double(9.0)
            n+ double(10.0)
            n+ double(11.0)
            n+ double(12.0)
            n+ double(13.0)
            n+ double(14.0)
            n+ double(15.0)
            n+ double(16.0)
        })
    }


    @Test
    fun `read+write for MutableMatrix4d should be symmetrical`() {
        simple<MutableMatrix4d, MutableMatrix4dSerializer>(MutableMatrix4d(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        ), NBTBuilder.list {
            n+ double(1.0)
            n+ double(2.0)
            n+ double(3.0)
            n+ double(4.0)
            n+ double(5.0)
            n+ double(6.0)
            n+ double(7.0)
            n+ double(8.0)
            n+ double(9.0)
            n+ double(10.0)
            n+ double(11.0)
            n+ double(12.0)
            n+ double(13.0)
            n+ double(14.0)
            n+ double(15.0)
            n+ double(16.0)
        })
    }


    @Test
    fun `read+write for MutableMatrix4d should modify the existing value`() {
        val goal = MutableMatrix4d(
            1.0, 2.0, 3.0, 4.0,
            5.0, 6.0, 7.0, 8.0,
            9.0, 10.0, 11.0, 12.0,
            13.0, 14.0, 15.0, 16.0
        )
        val target = MutableMatrix4d()
        val result = prism[Mirror.reflect<MutableMatrix4d>()].value.read(
            NBTBuilder.list {
                n+ double(1.0)
                n+ double(2.0)
                n+ double(3.0)
                n+ double(4.0)
                n+ double(5.0)
                n+ double(6.0)
                n+ double(7.0)
                n+ double(8.0)
                n+ double(9.0)
                n+ double(10.0)
                n+ double(11.0)
                n+ double(12.0)
                n+ double(13.0)
                n+ double(14.0)
                n+ double(15.0)
                n+ double(16.0)
            },
            target
        )
        assertSame(target, result)
        assertEquals(goal, target)
    }

    @Test
    fun `read+write for Quaternion should be symmetrical`() {
        simple<Quaternion, QuaternionSerializer>(Quaternion(1.0, 2.0, 3.0, 4.0), NBTBuilder.compound {
            "X" *= double(1.0)
            "Y" *= double(2.0)
            "Z" *= double(3.0)
            "W" *= double(4.0)
        })
    }
}
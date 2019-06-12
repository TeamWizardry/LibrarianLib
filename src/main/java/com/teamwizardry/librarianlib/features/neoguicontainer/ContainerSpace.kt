package com.teamwizardry.librarianlib.features.neoguicontainer

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.unaryMinus
import com.teamwizardry.librarianlib.features.math.Matrix3
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.math.coordinatespaces.CoordinateSpace2D
import com.teamwizardry.librarianlib.features.math.coordinatespaces.ScreenSpace
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

/**
 * The coordinate space relative to the container origin (top-left of the guicontainer rect)
 */
object ContainerSpace : CoordinateSpace2D {
    override val parentSpace: CoordinateSpace2D? = ScreenSpace

    internal var origin: Vec2d = vec(0, 0)

    override val matrix: Matrix3
        get() = Matrix3().also {
            val scaleFactor = ScaledResolution(Minecraft.getMinecraft()).scaleFactor.toDouble()
            it.scale(scaleFactor, scaleFactor)
            it.translate(origin)
        }.frozen()

    override val inverseMatrix: Matrix3
        get() = Matrix3().also {
            val scaleFactor = ScaledResolution(Minecraft.getMinecraft()).scaleFactor.toDouble()
            it.scale(1/scaleFactor, 1/scaleFactor)
            it.translate(-origin)
        }.frozen()

}

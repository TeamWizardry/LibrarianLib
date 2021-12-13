package com.teamwizardry.librarianlib.albedo.test

import net.minecraft.client.util.math.MatrixStack

abstract class AlbedoTestRenderer {
    var crashed: Boolean = false
    abstract fun render(matrices: MatrixStack, )
}
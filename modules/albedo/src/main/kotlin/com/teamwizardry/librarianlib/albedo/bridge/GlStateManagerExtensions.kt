package com.teamwizardry.librarianlib.albedo.bridge

import com.mojang.blaze3d.platform.GlStateManager

public object GlStateManagerExtensions {
    @JvmField
    public var customTextureTarget: Int = -1

    @JvmStatic
    public fun bindTexture(target: Int, texture: Int) {
        customTextureTarget = target
        GlStateManager._bindTexture(texture)
        customTextureTarget = -1
    }
}
package com.teamwizardry.librarianlib.common.container

import com.teamwizardry.librarianlib.common.container.internal.ContainerImpl
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by TheCodeWarrior
 */
@SaveInPlace
abstract class ContainerBase(val player: EntityPlayer) {
    abstract fun addTo(impl: ContainerImpl)
}

package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.features.kotlin.associateInPlace
import com.teamwizardry.librarianlib.features.math.aroundCenter
import com.teamwizardry.librarianlib.features.math.rotate
import com.teamwizardry.librarianlib.features.math.rotationMatrix
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
 * @author WireSegal
 * Created at 10:40 PM on 9/11/18.
 *
 * A helper class to rotate block AABBs by facing enums.
 */
class BoundingBoxTransformer(val baseBox: AxisAlignedBB) {
    companion object {
        private val transforms = EnumFacing.VALUES.associateInPlace {
            rotationMatrix(EnumFacing.NORTH, it).aroundCenter()
        }
    }

    private val transformed = EnumFacing.VALUES.associateInPlace { transforms[it]?.rotate(baseBox) }

    operator fun get(facing: EnumFacing) = transformed[facing]
}

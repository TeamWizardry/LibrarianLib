package com.teamwizardry.librarianlib.bloat.ragdoll.cloth

class HardLink3D(a: PointMass3D, b: PointMass3D, strength: Float) : Link3D(a, b, strength) {

    override fun resolve() {
        if (b.pin)
            return

        val posDiff = a.pos.subtract(b.pos)
        val d = posDiff.lengthVector()

        val difference = (distance - d) / d

        val translate = posDiff.scale(difference)

        b.pos = b.pos.subtract(translate)
    }

}

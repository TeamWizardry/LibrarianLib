package com.teamwizardry.librarianlib.features.animator.internal

import com.teamwizardry.librarianlib.features.animator.LerperHandler
import com.teamwizardry.librarianlib.features.animator.registerLerper

/**
 * TODO: Document file PrimitiveLerpers
 *
 * Created by TheCodeWarrior
 */
object PrimitiveLerpers {
    init {

        LerperHandler.registerLerper(Double::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }
        LerperHandler.registerLerper(Double::class.javaPrimitiveType!!) { from, to, frac ->
            from + (to - from) * frac
        }

        LerperHandler.registerLerper(Float::class.javaObjectType) { from, to, frac ->
            from + (to - from) * frac
        }
        LerperHandler.registerLerper(Float::class.javaPrimitiveType!!) { from, to, frac ->
            from + (to - from) * frac
        }

        LerperHandler.registerLerper(Long::class.javaObjectType) { from, to, frac ->
            (from + (to - from) * frac).toLong()
        }
        LerperHandler.registerLerper(Long::class.javaPrimitiveType!!) { from, to, frac ->
            (from + (to - from) * frac).toLong()
        }

        LerperHandler.registerLerper(Int::class.javaObjectType) { from, to, frac ->
            (from + (to - from) * frac).toInt()
        }
        LerperHandler.registerLerper(Int::class.javaPrimitiveType!!) { from, to, frac ->
            (from + (to - from) * frac).toInt()
        }

        LerperHandler.registerLerper(Short::class.javaObjectType) { from, to, frac ->
            (from + (to - from) * frac).toShort()
        }
        LerperHandler.registerLerper(Short::class.javaPrimitiveType!!) { from, to, frac ->
            (from + (to - from) * frac).toShort()
        }

        LerperHandler.registerLerper(Byte::class.javaObjectType) { from, to, frac ->
            (from + (to - from) * frac).toByte()
        }
        LerperHandler.registerLerper(Byte::class.javaPrimitiveType!!) { from, to, frac ->
            (from + (to - from) * frac).toByte()
        }

        LerperHandler.registerLerper(Char::class.javaObjectType) { from, to, frac ->
            from + ((to - from) * frac).toInt()
        }
        LerperHandler.registerLerper(Char::class.javaPrimitiveType!!) { from, to, frac ->
            from + ((to - from) * frac).toInt()
        }

        LerperHandler.registerLerper(Boolean::class.javaObjectType) { from, to, frac ->
            if (frac >= 1f) to else from
        }
        LerperHandler.registerLerper(Boolean::class.javaPrimitiveType!!) { from, to, frac ->
            if (frac >= 1f) to else from
        }
    }
}

package com.teamwizardry.librarianlib.core.util.lerp

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.TypeMirror

public abstract class Lerper<T> {
    public val type: TypeMirror

    public constructor(type: TypeMirror) {
        this.type = type
    }

    public constructor() {
        this.type = Mirror.reflectClass(this.javaClass).findSuperclass(Lerper::class.java)!!.typeParameters[0]
    }

    public abstract fun lerp(from: T, to: T, fraction: Float): T
}

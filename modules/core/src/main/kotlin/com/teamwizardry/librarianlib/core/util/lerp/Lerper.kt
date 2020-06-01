package com.teamwizardry.librarianlib.core.util.lerp

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.type.TypeMirror

abstract class Lerper<T> {
    val type: TypeMirror

    constructor(type: TypeMirror) {
        this.type = type
    }

    constructor() {
        this.type = Mirror.reflectClass(this.javaClass).findSuperclass(Lerper::class.java)!!.typeParameters[0]
    }

    abstract fun lerp(from: T, to: T, fraction: Float): T
}

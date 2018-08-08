package com.teamwizardry.librarianlib.features.math.interpolate.numeric

import com.teamwizardry.librarianlib.features.math.interpolate.InterpCombine
import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp

class InterpFloatAdd(first: InterpFunction<Float>, second: InterpFunction<Float>):
        InterpCombine<Float, Float, Float>(first, second, { a, b -> a + b}){
    constructor(first: InterpFunction<Float>, second: Float): this(first, StaticInterp(second))
    constructor(first: Float, second: InterpFunction<Float>): this(StaticInterp(first), second)
}

class InterpFloatSubtract(first: InterpFunction<Float>, second: InterpFunction<Float>):
        InterpCombine<Float, Float, Float>(first, second, { a, b -> a - b}){
    constructor(first: InterpFunction<Float>, second: Float): this(first, StaticInterp(second))
    constructor(first: Float, second: InterpFunction<Float>): this(StaticInterp(first), second)
}

class InterpFloatMultiply(first: InterpFunction<Float>, second: InterpFunction<Float>):
        InterpCombine<Float, Float, Float>(first, second, { a, b -> a * b}){
    constructor(first: InterpFunction<Float>, second: Float): this(first, StaticInterp(second))
    constructor(first: Float, second: InterpFunction<Float>): this(StaticInterp(first), second)
}

class InterpFloatDivide(first: InterpFunction<Float>, second: InterpFunction<Float>):
        InterpCombine<Float, Float, Float>(first, second, { a, b -> a / b}){
    constructor(first: InterpFunction<Float>, second: Float): this(first, StaticInterp(second))
    constructor(first: Float, second: InterpFunction<Float>): this(StaticInterp(first), second)
}

operator fun InterpFunction<Float>.plus(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatAdd(this, other)
}
operator fun InterpFunction<Float>.plus(other: Float): InterpFunction<Float> {
    return InterpFloatAdd(this, other)
}
operator fun Float.plus(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatAdd(this, other)
}

operator fun InterpFunction<Float>.minus(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatSubtract(this, other)
}
operator fun InterpFunction<Float>.minus(other: Float): InterpFunction<Float> {
    return InterpFloatSubtract(this, other)
}
operator fun Float.minus(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatSubtract(this, other)
}

operator fun InterpFunction<Float>.times(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatMultiply(this, other)
}
operator fun InterpFunction<Float>.times(other: Float): InterpFunction<Float> {
    return InterpFloatMultiply(this, other)
}
operator fun Float.times(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatMultiply(this, other)
}

operator fun InterpFunction<Float>.div(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatDivide(this, other)
}
operator fun InterpFunction<Float>.div(other: Float): InterpFunction<Float> {
    return InterpFloatDivide(this, other)
}
operator fun Float.div(other: InterpFunction<Float>): InterpFunction<Float> {
    return InterpFloatDivide(this, other)
}

operator fun InterpFunction<Float>.unaryMinus(): InterpFunction<Float> {
    return InterpFloatMultiply(this, -1f)
}

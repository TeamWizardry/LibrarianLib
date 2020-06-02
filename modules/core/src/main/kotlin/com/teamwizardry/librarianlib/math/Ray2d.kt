package com.teamwizardry.librarianlib.math

import kotlin.math.max
import kotlin.math.min

class Ray2d(originX: Double, originY: Double, directionX: Double, directionY: Double) {

    constructor(origin: Vec2d, direction: Vec2d) : this(origin.x, origin.y, direction.x, direction.y)

    /** The distance, measured in the multiple of the direction vector used for this trace (hit point = origin + distance * direction) */
    var hitDistance: Double = Double.NaN
        private set

    var hitX: Double = Double.NaN
        private set
    var hitY: Double = Double.NaN
        private set
    val hit: Vec2d?
        get() = if(hitX.isNaN() || hitY.isNaN()) null else vec(hitX, hitY)

    var hitNormalX: Double = Double.NaN
        private set
    var hitNormalY: Double = Double.NaN
        private set
    val hitNormal: Vec2d?
        get() = if(hitNormalX.isNaN() || hitNormalY.isNaN()) null else vec(hitNormalX, hitNormalY)

    var originX: Double = originX
        private set
    var originY: Double = originY
        private set
    val origin: Vec2d
        get() = vec(originX, originY)

    var directionX: Double = directionX
        private set
    var directionY: Double = directionY
        private set
    val direction: Vec2d
        get() = vec(directionX, directionY)

    private var invX: Double = 1 / directionX
    private var invY: Double = 1 / directionY

    fun reset(origin: Vec2d, direction: Vec2d) = reset(origin.x, origin.y, direction.x, direction.y)

    fun reset(originX: Double, originY: Double, directionX: Double, directionY: Double) {
        this.hitDistance = Double.NaN
        this.hitX = Double.NaN
        this.hitY = Double.NaN
        this.hitNormalX = Double.NaN
        this.hitNormalY = Double.NaN

        this.originX = originX
        this.originY = originY
        this.directionX = directionX
        this.directionY = directionY

        this.invX = 1 / directionX
        this.invY = 1 / directionY
    }

    // https://tavianator.com/fast-branchless-raybounding-box-intersections-part-2-nans/
    fun raytrace(minX: Double, minY: Double, maxX: Double, maxY: Double): Boolean {
        val tx1 = (minX - originX) * invX
        val tx2 = (maxX - originX) * invX

        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (minY - originY) * invY
        val ty2 = (maxY - originY) * invY

        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))


        if (tmax >= tmin && tmax >= 0 && tmin >= 0 && (hitDistance.isNaN() || tmin < hitDistance)) {
            hitDistance = tmin
            hitX = originX + hitDistance * originX
            hitY = originY + hitDistance * originY
            hitNormalX = if (tmin == tx1) -1.0 else if (tmin == tx2) 1.0 else 0.0
            hitNormalY = if (tmin == ty1) -1.0 else if (tmin == ty2) 1.0 else 0.0
            return true
        }
        return false
    }

}
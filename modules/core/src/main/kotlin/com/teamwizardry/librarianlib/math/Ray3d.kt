package com.teamwizardry.librarianlib.math

import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

public class Ray3d(originX: Double, originY: Double, originZ: Double, directionX: Double, directionY: Double, directionZ: Double) {

    public constructor(origin: Vec3d, direction: Vec3d): this(origin.x, origin.y, origin.z, direction.x, direction.y, direction.z)

    /** The distance, measured in the multiple of the direction vector used for this trace (hit point = origin + distance * direction) */
    public var hitDistance: Double = Double.NaN
        private set

    public var hitX: Double = Double.NaN
        private set
    public var hitY: Double = Double.NaN
        private set
    public var hitZ: Double = Double.NaN
        private set
    public val hit: Vec3d?
        get() = if (hitX.isNaN() || hitY.isNaN() || hitZ.isNaN()) null else vec(hitX, hitY, hitZ)

    public var hitNormalX: Double = Double.NaN
        private set
    public var hitNormalY: Double = Double.NaN
        private set
    public var hitNormalZ: Double = Double.NaN
        private set
    public val hitNormal: Vec3d?
        get() = if (hitNormalX.isNaN() || hitNormalY.isNaN() || hitNormalZ.isNaN()) null else vec(hitNormalX, hitNormalY, hitNormalZ)

    public var originX: Double = originX
        private set
    public var originY: Double = originY
        private set
    public var originZ: Double = originZ
        private set
    public val origin: Vec3d
        get() = vec(originX, originY, originZ)

    public var directionX: Double = directionX
        private set
    public var directionY: Double = directionY
        private set
    public var directionZ: Double = directionZ
        private set
    public val direction: Vec3d
        get() = vec(directionX, directionY, directionZ)

    private var invX: Double = 1 / directionX
    private var invY: Double = 1 / directionY
    private var invZ: Double = 1 / directionZ

    public fun reset(origin: Vec3d, direction: Vec3d) {
        reset(origin.x, origin.y, origin.z, direction.x, direction.y, direction.z)
    }

    public fun reset(originX: Double, originY: Double, originZ: Double, directionX: Double, directionY: Double, directionZ: Double) {
        this.hitDistance = Double.NaN
        this.hitX = Double.NaN
        this.hitY = Double.NaN
        this.hitZ = Double.NaN
        this.hitNormalX = Double.NaN
        this.hitNormalY = Double.NaN
        this.hitNormalZ = Double.NaN

        this.originX = originX
        this.originY = originY
        this.originZ = originZ
        this.directionX = directionX
        this.directionY = directionY
        this.directionZ = directionZ

        this.invX = 1 / directionX
        this.invY = 1 / directionY
        this.invZ = 1 / directionZ
    }

    // https://tavianator.com/fast-branchless-raybounding-box-intersections-part-2-nans/
    public fun raytrace(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        val tx1 = (minX - originX) * invX
        val tx2 = (maxX - originX) * invX

        var tmin = min(tx1, tx2)
        var tmax = max(tx1, tx2)

        val ty1 = (minY - originY) * invY
        val ty2 = (maxY - originY) * invY

        tmin = max(tmin, min(ty1, ty2))
        tmax = min(tmax, max(ty1, ty2))

        val tz1 = (minZ - originZ) * invZ
        val tz2 = (maxZ - originZ) * invZ

        tmin = max(tmin, min(tz1, tz2))
        tmax = min(tmax, max(tz1, tz2))

        if (tmax >= tmin && tmax >= 0 && tmin >= 0 && (hitDistance.isNaN() || tmin < hitDistance)) {
            hitDistance = tmin
            hitX = originX + hitDistance * originX
            hitY = originY + hitDistance * originY
            hitZ = originZ + hitDistance * originZ
            hitNormalX = if (tmin == tx1) -1.0 else if (tmin == tx2) 1.0 else 0.0
            hitNormalY = if (tmin == ty1) -1.0 else if (tmin == ty2) 1.0 else 0.0
            hitNormalZ = if (tmin == tz1) -1.0 else if (tmin == tz2) 1.0 else 0.0
            return true
        }
        return false
    }
}
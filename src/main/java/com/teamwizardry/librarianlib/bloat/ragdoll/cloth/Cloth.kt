package com.teamwizardry.librarianlib.bloat.ragdoll.cloth

import com.google.common.annotations.VisibleForTesting
import com.google.common.collect.ImmutableList
import com.teamwizardry.librarianlib.common.util.math.Geometry
import com.teamwizardry.librarianlib.bloat.MathUtil
import com.teamwizardry.librarianlib.common.util.math.Matrix4
import com.teamwizardry.librarianlib.common.util.math.Sphere
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import java.util.*

class Cloth(var top: Array<Vec3d>, var height: Int, var size: Vec3d) {

    lateinit var masses: Array<Array<PointMass3D>>
    var links: MutableList<Link3D> = ArrayList()
    var hardLinks: MutableList<Link3D> = ArrayList()
    var solvePasses = 5
    var stretch = 1f
    var shear = 1f
    var flex = 1f
    var air = 1.5f
    var gravity = Vec3d(0.0, -0.01, 0.0)
    var relativePositions: Map<PointMass3D, Vec3d> = HashMap()

    init {
        this.stretch = 0.8f
        this.shear = 0.8f
        this.flex = 0.9f
        init()
    }

    fun updateRelative(pos: Vec3d, rotation: Vec3d) {
        val matrix = Matrix4()
        matrix.translate(pos)
        matrix.rotate(Math.toRadians(rotation.xCoord), Vec3d(1.0, 0.0, 0.0))
        matrix.rotate(Math.toRadians(rotation.yCoord), Vec3d(0.0, 1.0, 0.0))
        matrix.rotate(Math.toRadians(rotation.zCoord), Vec3d(0.0, 0.0, 1.0))
        for ((key, value) in relativePositions) {
            val trans = matrix.apply(value)
            key.origPos = key.pos
            key.pos = trans
        }
    }

    fun init() {
        masses = Array<Array<PointMass3D>>(height) { y -> Array<PointMass3D>(top.size) { x -> PointMass3D(top[x].add(size * y), 0.1f) } }
        links = ArrayList<Link3D>()

        for (x in masses.indices) {
            for (z in 0..masses[x].size - 1) {

                if (x + 1 < masses.size)
                    hardLinks.add(HardLink3D(masses[x][z], masses[x + 1][z], 1f))

                if (x + 1 < masses.size)
                    links.add(Link3D(masses[x][z], masses[x + 1][z], stretch / solvePasses))
                if (z + 1 < masses[x].size && x != 0)
                    links.add(Link3D(masses[x][z], masses[x][z + 1], stretch / solvePasses))

                if (x + 1 < masses.size && z + 1 < masses[x].size)
                    links.add(Link3D(masses[x][z], masses[x + 1][z + 1], shear / solvePasses))
                if (x + 1 < masses.size && z - 1 >= 0)
                    links.add(Link3D(masses[x][z], masses[x + 1][z - 1], shear / solvePasses))
            }
        }

        for (x in masses.indices) {
            for (z in 0..masses[x].size - 1) {
                if (x + 2 < masses.size) {
                    val dist = (masses[x][z].pos.subtract(masses[x + 1][z].pos).lengthVector() + masses[x + 1][z].pos.subtract(masses[x + 2][z].pos).lengthVector()).toFloat() // even if initialized bent, try to keep flat.
                    links.add(Link3D(masses[x][z], masses[x + 2][z], dist, flex / solvePasses))
                }
                if (z + 2 < masses[x].size) {
                    val dist = (masses[x][z].pos.subtract(masses[x][z + 1].pos).lengthVector() + masses[x][z + 1].pos.subtract(masses[x][z + 2].pos).lengthVector()).toFloat() // even if initialized bent, try to keep flat.
                    links.add(Link3D(masses[x][z], masses[x][z + 2], dist, flex / solvePasses))
                }

                if (x + 2 < masses.size && z + 2 < masses[x].size) {
                    val dist = (masses[x][z].pos.subtract(masses[x + 1][z + 1].pos).lengthVector() + masses[x + 1][z + 1].pos.subtract(masses[x + 2][z + 2].pos).lengthVector()).toFloat() // even if initialized bent, try to keep flat.
                    links.add(Link3D(masses[x][z], masses[x + 2][z + 2], dist, flex / solvePasses))
                }
                if (x + 2 < masses.size && z - 2 > 0) {
                    val dist = (masses[x][z].pos.subtract(masses[x + 1][z - 1].pos).lengthVector() + masses[x + 1][z - 1].pos.subtract(masses[x + 2][z - 2].pos).lengthVector()).toFloat() // even if initialized bent, try to keep flat.
                    links.add(Link3D(masses[x][z], masses[x + 2][z - 2], dist, flex / solvePasses))
                }
            }
        }
    }

    /**
     * Calls [.pushOutPoint] for all the points in the mesh
     * @param aabbs
     * *
     * @param boxes
     */
    private fun pushOutPoints(aabbs: List<AxisAlignedBB>, spheres: List<Sphere>) {
        for (column in masses) {
            for (point in column) {
                pushOutPoint(point, aabbs, spheres)
            }
        }
    }

    /**
     * Pushes the point out of the passed AABBs and Boxes
     * @param point
     * *
     * @param aabbs
     * *
     * @param boxes
     */
    private fun pushOutPoint(point: PointMass3D, aabbs: List<AxisAlignedBB>, spheres: List<Sphere>) {
        if (point.pin)
            return
        for (aabb in aabbs) {
            //			point.pos = AABBUtils.closestOutsidePoint(aabb, point.pos);
        }
        for (sphere in spheres) {
            point.pos = sphere.fix(point.pos)
        }
    }

    /**
     * Calls [.collidePoint] to all the points in the mesh
     * @param aabbs
     * *
     * @param boxes
     */
    private fun collidePoints(aabbs: List<AxisAlignedBB>, spheres: List<Sphere>) {
        for (column in masses) {
            for (point in column) {
                collidePoint(point, aabbs, spheres)
            }
        }
    }

    /**
     * Applies motion collision for the passed point using the passed AABBs and Boxes.
     * @param point
     * *
     * @param aabbs
     * *
     * @param boxes
     */
    private fun collidePoint(point: PointMass3D, aabbs: List<AxisAlignedBB>, spheres: List<Sphere>) {
        if (point.pin)
            return
        val friction = 0.2
        point.friction = null
        for (aabb in aabbs) {
            val res = calculateIntercept(aabb, point, true)
            if (res != null) {
                point.pos = res
            }
        }
        for (aabb in aabbs) {
            val res = calculateIntercept(aabb, point, false)
            if (res != null) {
                point.pos = res
            }
        }
        for (sphere in spheres) {
            point.pos = sphere.trace(point.origPos, point.pos)
        }
        point.applyMotion((point.friction ?: Vec3d.ZERO) * -friction )
    }

    private fun applyMotionToPoints() {
        for (x in masses.indices) {
            for (y in 0..masses[x].size - 1) {
                applyMotionToPoint(x, y, masses[x][y])
            }
        }
    }

    private fun applyMotionToPoint(x: Int, y: Int, point: PointMass3D) {

        if (point.pin)
            return

        val lastMotion = point.pos.subtract(point.prevPos)
        point.applyMotion(lastMotion) // existing motion
        point.applyForce(gravity) // gravity

        val wind = Vec3d(0.0, 0.0, 1.0 / 20.0).subtract(lastMotion)
        //		wind = Vec3d.ZERO;
        var normal = Vec3d.ZERO

        if (x > 0 && y > 0) {
            normal = normal.add(Geometry.getNormal(point.origPos, masses[x][y - 1].origPos, masses[x - 1][y].origPos))

        }

        if (x > 0 && y + 1 < masses[x].size) {
            normal = normal.add(Geometry.getNormal(point.origPos, masses[x][y + 1].origPos, masses[x - 1][y].origPos))
        }

        if (x + 1 < masses.size && y + 1 < masses[x].size) {
            normal = normal.add(Geometry.getNormal(point.origPos, masses[x][y + 1].origPos, masses[x + 1][y].origPos))
        }

        if (x + 1 < masses.size && y > 0) {
            normal = normal.add(Geometry.getNormal(point.origPos, masses[x][y - 1].origPos, masses[x + 1][y].origPos))
        }

        normal = normal.normalize()
        val windNormal = wind.normalize()

        val angle = Math.acos(MathUtil.clamp(windNormal.dotProduct(normal), -1.0, 1.0))
        if (angle > Math.PI / 2)
            normal = normal.scale(-1.0)

        // https://books.google.com/books?id=x5cLAQAAIAAJ&pg=PA5&lpg=PA5&dq=wind+pressure+on+a+flat+angled+surface&source=bl&ots=g090hiOfxv&sig=MqZQhLMozsMNndJtkA1R_bk5KiA&hl=en&sa=X&ved=0ahUKEwiozMW2z_vNAhUD7yYKHeqvBVcQ6AEILjAC#v=onepage&q&f=false
        // page 5-6. I'm using formula (5)
        // wind vector length squared is flat pressure. All the other terms can
        // be changed in the air coefficent.
        val force = normal.add(windNormal).normalize().scale(Math.pow(wind.lengthVector(), 2.0) * angle / (Math.PI / 4))

        point.applyForce(force.scale(air.toDouble()))

        point.friction = null
    }

    private fun getAABBs(e: Entity): List<AxisAlignedBB> {
        var minX = java.lang.Double.MAX_VALUE
        var minY = java.lang.Double.MAX_VALUE
        var minZ = java.lang.Double.MAX_VALUE
        var maxX = -java.lang.Double.MAX_VALUE
        var maxY = -java.lang.Double.MAX_VALUE
        var maxZ = -java.lang.Double.MAX_VALUE

        for (x in masses.indices) {
            for (y in 0..masses[x].size - 1) {
                val mass = masses[x][y]
                minX = Math.min(minX, Math.min(mass.pos.xCoord, mass.origPos.xCoord))
                minY = Math.min(minY, Math.min(mass.pos.yCoord, mass.origPos.yCoord))
                minZ = Math.min(minZ, Math.min(mass.pos.zCoord, mass.origPos.zCoord))

                if (maxX - minX > 10)
                    minX = maxX - 10
                if (maxY - minY > 10)
                    minY = maxY - 10
                if (maxZ - minZ > 10)
                    minZ = maxZ - 10

                maxX = Math.max(maxX, Math.max(mass.pos.xCoord, mass.origPos.xCoord))
                maxY = Math.max(maxY, Math.max(mass.pos.yCoord, mass.origPos.yCoord))
                maxZ = Math.max(maxZ, Math.max(mass.pos.zCoord, mass.origPos.zCoord))

                if (maxX - minX > 10)
                    maxX = minX + 10
                if (maxY - minY > 10)
                    maxY = minY + 10
                if (maxZ - minZ > 10)
                    maxZ = minZ + 10
            }
        }
        val m = 0.5
        val checkAABB = AxisAlignedBB(minX - m, minY - m, minZ - m, maxX + m, maxY + m, maxZ + m)
        val aabbs = e.worldObj.getCollisionBoxes(checkAABB)
        val entities = e.worldObj.getEntitiesWithinAABBExcludingEntity(null, checkAABB)
        for (entity in entities) {
            //			aabbs.add(entity.getEntityBoundingBox());
        }

        return aabbs
    }

    fun tick(e: Entity, spheres: List<Sphere>) {

        var aabbs = getAABBs(e)
        pushOutPoints(aabbs, spheres)

        for (column in masses) {
            for (point in column) {
                if (!point.pin) {
                    point.prevPos = point.pos
                    point.origPos = point.pos
                }
            }
        }

        applyMotionToPoints()

        aabbs = getAABBs(e)

        collidePoints(aabbs, spheres)
        pushOutPoints(aabbs, spheres)

        for (i in 0..solvePasses - 1) {
            for (link in links) {
                link.resolve()
            }
            for (link in hardLinks) {
                link.resolve()
            }
            collidePoints(ImmutableList.of<AxisAlignedBB>(), spheres)
        }

        collidePoints(aabbs, spheres)
        pushOutPoints(aabbs, spheres)

        for (link in hardLinks) {
            val posDiff = link.a.pos.subtract(link.b.pos)
            val d = posDiff.lengthVector()

            val difference = d - link.distance
            if (difference > link.distance)
                link.resolve()
        }

        //        collidePoints(ImmutableList.of(), boxes);
        //		pushOutPoints(ImmutableList.of(), boxes);
    }

    fun calculateIntercept(aabb: AxisAlignedBB, point: PointMass3D, yOnly: Boolean): Vec3d? {
        val vecA = point.origPos
        val vecB = point.pos

        var vecX: Vec3d? = null
        var vecY: Vec3d? = null
        var vecZ: Vec3d? = null

        if (vecA.yCoord > vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.maxY, vecA, vecB)
        }

        if (vecA.yCoord < vecB.yCoord) {
            vecY = collideWithYPlane(aabb, aabb.minY, vecA, vecB)
        }

        if (vecY != null) {
            point.friction = Vec3d(vecB.xCoord - vecY.xCoord, 0.0, vecB.zCoord - vecY.zCoord)
            return Vec3d(vecB.xCoord, vecY.yCoord, vecB.zCoord)
        }

        if (yOnly)
            return null

        if (vecA.xCoord > vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.maxX, vecA, vecB)
        }

        if (vecA.xCoord < vecB.xCoord) {
            vecX = collideWithXPlane(aabb, aabb.minX, vecA, vecB)
        }

        if (vecX != null) {
            point.friction = Vec3d(0.0, vecB.yCoord - vecX.yCoord, vecB.zCoord - vecX.zCoord)
            return Vec3d(vecX.xCoord, vecB.yCoord, vecB.zCoord)
        }

        if (vecA.zCoord > vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.maxZ, vecA, vecB)
        }

        if (vecA.zCoord < vecB.zCoord) {
            vecZ = collideWithZPlane(aabb, aabb.minZ, vecA, vecB)
        }

        if (vecZ != null) {
            point.friction = Vec3d(vecB.xCoord - vecZ.xCoord, vecB.yCoord - vecZ.yCoord, 0.0)
            return Vec3d(vecB.xCoord, vecB.yCoord, vecZ.zCoord)
        }

        return null
    }

    internal fun min(a: Vec3d?, b: Vec3d?): Vec3d? {
        if (a == null && b == null)
            return null
        if (a != null && b == null)
            return a
        if (a == null && b != null)
            return b
        if (a == null || b == null)
            return null

        if (b.squareDistanceTo(Vec3d.ZERO) < a.squareDistanceTo(Vec3d.ZERO))
            return b
        return a
    }

    @VisibleForTesting
    internal fun collideWithXPlane(aabb: AxisAlignedBB, p_186671_1_: Double, p_186671_3_: Vec3d, p_186671_4_: Vec3d): Vec3d? {
        val vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_)
        return if (vec3d != null && this.intersectsWithYZ(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    internal fun collideWithYPlane(aabb: AxisAlignedBB, p_186663_1_: Double, p_186663_3_: Vec3d, p_186663_4_: Vec3d): Vec3d? {
        val vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_)
        return if (vec3d != null && this.intersectsWithXZ(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    internal fun collideWithZPlane(aabb: AxisAlignedBB, p_186665_1_: Double, p_186665_3_: Vec3d, p_186665_4_: Vec3d): Vec3d? {
        val vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_)
        return if (vec3d != null && this.intersectsWithXY(aabb, vec3d)) vec3d else null
    }

    @VisibleForTesting
    fun intersectsWithYZ(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        val m = -0.0
        return vec.yCoord > aabb.minY + m && vec.yCoord < aabb.maxY - m && vec.zCoord > aabb.minZ + m && vec.zCoord < aabb.maxZ - m
    }

    @VisibleForTesting
    fun intersectsWithXZ(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        val m = -0.0
        return vec.xCoord > aabb.minX + m && vec.xCoord < aabb.maxX - m && vec.zCoord > aabb.minZ + m && vec.zCoord < aabb.maxZ - m
    }

    @VisibleForTesting
    fun intersectsWithXY(aabb: AxisAlignedBB, vec: Vec3d): Boolean {
        return vec.xCoord > aabb.minX && vec.xCoord < aabb.maxX && vec.yCoord > aabb.minY && vec.yCoord < aabb.maxY
    }

}

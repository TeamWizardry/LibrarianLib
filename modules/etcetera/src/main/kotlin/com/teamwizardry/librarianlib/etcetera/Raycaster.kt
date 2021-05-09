package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.core.util.QUILT_TODO
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import java.util.function.Predicate

/**
 * A class designed to efficiently raytrace collisions with the world. This class uses custom raytracing code to
 * reduce the number short-lived objects created and to improve the performance of long rays.
 */
public class Raycaster {
    /**
     * The type of hit that occurred, if any.
     */
    public var hitType: HitType = HitType.NONE
        private set

    /**
     * The X component of the impacted block's position, or 0 if no impact occurred.
     */
    public var blockX: Int = 0
        private set

    /**
     * The Y component of the impacted block's position, or 0 if no impact occurred.
     */
    public var blockY: Int = 0
        private set

    /**
     * The Z component of the impacted block's position, or 0 if no impact occurred.
     */
    public var blockZ: Int = 0
        private set

    /**
     * The entity that was hit
     */
    public var entity: Entity? = null
        private set

    /**
     * The fraction along the raycast that an impact occurred, or 1.0 if no impact occurred
     */
    public var fraction: Double = 0.0
        private set

    /**
     * The hit position, or the end position if no hit occurred. Computed as `start + (end - start) * fraction`
     */
    public val hitX: Double
        get() = startX + (endX - startX) * fraction

    /**
     * The hit position, or the end position if no hit occurred. Computed as `start + (end - start) * fraction`
     */
    public val hitY: Double
        get() = startY + (endY - startY) * fraction

    /**
     * The hit position, or the end position if no hit occurred. Computed as `start + (end - start) * fraction`
     */
    public val hitZ: Double
        get() = startZ + (endZ - startZ) * fraction

    /**
     * The X component of the impacted face's normal, or 0.0 if no impact occurred
     */
    public var normalX: Double = 0.0
        private set

    /**
     * The Y component of the impacted face's normal, or 0.0 if no impact occurred
     */
    public var normalY: Double = 0.0
        private set

    /**
     * The Z component of the impacted face's normal, or 0.0 if no impact occurred
     */
    public var normalZ: Double = 0.0
        private set

    /**
     * The configured start position
     */
    public var startX: Double = 0.0
        private set

    /**
     * The configured start position
     */
    public var startY: Double = 0.0
        private set

    /**
     * The configured start position
     */
    public var startZ: Double = 0.0
        private set

    /**
     * The configured end position
     */
    public var endX: Double = 0.0
        private set

    /**
     * The configured end position
     */
    public var endY: Double = 0.0
        private set

    /**
     * The configured end position
     */
    public var endZ: Double = 0.0
        private set

    /**
     * Cast the ray through the passed world, colliding with blocks using the specified mode, ignoring fluid and
     * entities.
     *
     * The result of the raycast is made available as properties of this raycaster. It is ***vitally*** important that
     * you call [reset] once you're done with the result to prepare for the next raycast.
     *
     * @see hitType
     * @see blockX
     * @see blockY
     * @see blockZ
     * @see entity
     * @see fraction
     * @see hitX
     * @see hitY
     * @see hitZ
     * @see normalX
     * @see normalY
     * @see normalZ
     */
    public fun cast(
        world: World,
        blockMode: BlockMode,
        startX: Double, startY: Double, startZ: Double, endX: Double, endY: Double, endZ: Double
    ) {
        cast(world, blockMode, FluidMode.NONE, null, startX, startY, startZ, endX, endY, endZ)
    }

    /**
     * Cast the ray through the passed world, colliding with blocks and fluids using the specified modes, and colliding
     * with entities according to the specified filter, if present. An [entityFilter] of `null` will ignore entities.
     *
     * The result of the raycast is made available as properties of this raycaster. It is ***vitally*** important that
     * you call [reset] once you're done with the result to prepare for the next raycast and avoid leaking [entity].
     *
     * @param blockMode The type of collisions to make with solid blocks
     * @param fluidMode The type of collisions to make with fluids
     * @param entityFilter If non-null, a predicate dictating which entities to collide against.
     *
     * @see hitType
     * @see blockX
     * @see blockY
     * @see blockZ
     * @see entity
     * @see fraction
     * @see hitX
     * @see hitY
     * @see hitZ
     * @see normalX
     * @see normalY
     * @see normalZ
     */
    public fun cast(
        world: World,
        blockMode: BlockMode,
        fluidMode: FluidMode,
        entityFilter: Predicate<Entity>?,
        startX: Double, startY: Double, startZ: Double, endX: Double, endY: Double, endZ: Double
    ) {
        reset()
        this.startX = startX
        this.startY = startY
        this.startZ = startZ

        this.endX = endX
        this.endY = endY
        this.endZ = endZ

        invVelX = 1.0 / (endX - startX)
        invVelY = 1.0 / (endY - startY)
        invVelZ = 1.0 / (endZ - startZ)

        if (blockMode != BlockMode.NONE || fluidMode != FluidMode.NONE) {
            castBlocks(world, blockMode, fluidMode)
        }
        if (entityFilter != null) {
            castEntities(world, entityFilter)
        }
    }

    /**
     * Resets the state
     */
    public fun reset() {
        hitType = HitType.NONE
        fraction = 1.0
        normalX = 0.0
        normalY = 0.0
        normalZ = 0.0
        blockX = 0
        blockY = 0
        blockZ = 0
        entity = null

        startX = 0.0
        startY = 0.0
        startZ = 0.0
        endX = 0.0
        endY = 0.0
        endZ = 0.0
        invVelX = 0.0
        invVelY = 0.0
        invVelZ = 0.0
        raycaster.reset()
    }

    public enum class BlockMode {
        /**
         * Ignore blocks
         */
        NONE,

        /**
         * Use the collision shape of the block
         */
        COLLISION,

        /**
         * Use the visual shape of the block (like when checking what block to click on)
         */
        VISUAL;
    }

    public enum class FluidMode {
        /**
         * Ignore fluids
         */
        NONE,

        /**
         * Only return hits on source blocks (like when using a bucket)
         */
        SOURCE,

        /**
         * Return hits on any fluid
         */
        ANY;
    }

    public enum class HitType {
        /**
         * No hit occurred
         */
        NONE,

        /**
         * The ray hit a block
         */
        BLOCK,

        /**
         * The ray hit a fluid
         */
        FLUID,

        /**
         * The ray hit an entity
         */
        ENTITY;
    }

    // v============================ Implementation ===========================v
    // Note: Because each hit test is reusing the same `DirectRaycaster`, tests will only succeed if they are closer
    // than the closest hit so far. This allows us to trivially cast against multiple types of object.

    private val intersectingIterator = IntersectingBlocksIterator()
    private val raycaster = DirectRaycaster()

    // v-------------------------------- Blocks -------------------------------v
    private val mutablePos = BlockPos.Mutable()

    private var invVelX: Double = 0.0
    private var invVelY: Double = 0.0
    private var invVelZ: Double = 0.0

    /**
     * The implementation of block raycasting.
     */
    private fun castBlocks(world: World, blockMode: BlockMode, fluidMode: FluidMode) {
        // Only blocks the ray directly passes through are checked.
        intersectingIterator.reset(
            startX, startY, startZ,
            endX, endY, endZ
        )
        for (block in intersectingIterator) {
            if (castBlock(world, blockMode, fluidMode, block.x, block.y, block.z))
                break // short-circuit at the first hit since we iterate near to far
        }
    }

    /**
     * Cast the ray through the passed block.
     * @return true if the block was hit
     */
    private fun castBlock(world: World, blockMode: BlockMode, fluidMode: FluidMode, blockX: Int, blockY: Int, blockZ: Int): Boolean {
        mutablePos.set(blockX, blockY, blockZ)
        val hitBlock = when (blockMode) {
            BlockMode.NONE -> {
                false
            }
            BlockMode.COLLISION -> {
                val state = world.getBlockState(mutablePos)
                castShape(blockX, blockY, blockZ, state.getCollisionShape(world, mutablePos))
            }
            BlockMode.VISUAL -> {
                val state = world.getBlockState(mutablePos)
                castShape(blockX, blockY, blockZ, state.getRaycastShape(world, mutablePos))
            }
        }
        if (hitBlock) {
            entity = null
            this.blockX = blockX
            this.blockY = blockY
            this.blockZ = blockZ
            hitType = HitType.BLOCK
        }

        val hitFluid = when (fluidMode) {
            FluidMode.NONE -> {
                false
            }
            FluidMode.SOURCE -> {
                val state = world.getFluidState(mutablePos)
                if (state.isStill)
                    castShape(blockX, blockY, blockZ, state.getShape(world, mutablePos))
                else
                    false
            }
            FluidMode.ANY -> {
                val state = world.getFluidState(mutablePos)
                castShape(blockX, blockY, blockZ, state.getShape(world, mutablePos))
            }
        }
        if (hitFluid) {
            entity = null
            this.blockX = blockX
            this.blockY = blockY
            this.blockZ = blockZ
            hitType = HitType.FLUID
        }
        return hitBlock || hitFluid
    }

    /**
     * The ray start relative to the block currently being tested. Used by [boxConsumer]
     */
    private var relativeStartX: Double = 0.0
    private var relativeStartY: Double = 0.0
    private var relativeStartZ: Double = 0.0

    /**
     * This is reset to false before each shape is tested, and is set to true if any of the boxes sent to [boxConsumer]
     * resulted in a hit.
     *
     * [VoxelShape.forEachBox] just passes each box to the function, so we need somewhere external to track whether
     * there was a hit.
     */
    private var didHitShape = false

    /**
     * [VoxelShape.forEachBox] expects a [VoxelShapes.ILineConsumer]. While I could make the raycaster implement that
     * interface, it would only serve to clutter the API.
     */
    private val boxConsumer = VoxelShapes.BoxConsumer { minX, minY, minZ, maxX, maxY, maxZ ->
        val raycaster = raycaster
        if (raycaster.cast(
                true,
                minX, minY, minZ,
                maxX, maxY, maxZ,
                relativeStartX, relativeStartY, relativeStartZ,
                invVelX, invVelY, invVelZ
            )) {
            fraction = raycaster.distance
            normalX = raycaster.normalX
            normalY = raycaster.normalY
            normalZ = raycaster.normalZ
            didHitShape = true
        }
    }

    /**
     * Cast the ray through the passed shape.
     */
    private fun castShape(blockX: Int, blockY: Int, blockZ: Int, shape: VoxelShape): Boolean {
        if (shape === VoxelShapes.empty())
            return false

        // the bounding boxes that get fed to [boxConsumer] are all relative to the block (they aren't in absolute world
        // coordinates), so we have to transform the start point to be relative to the block.
        relativeStartX = startX - blockX
        relativeStartY = startY - blockY
        relativeStartZ = startZ - blockZ
        didHitShape = false
        shape.forEachBox(boxConsumer)
        return didHitShape
    }

    // v------------------------------- Entities ------------------------------v

    private val entityList = ArrayList<Entity>()

    /**
     * The chunks we've already tested against. [castEntityChunks] runs four times with slightly different chunks, so
     * this allows us to skip chunks we've already checked.
     */
    private val visitedEntityChunks: LongSet = LongOpenHashSet()

    /**
     * The implementation of entity raycasting. This checks against entities on a per-chunk basis
     */
    private fun castEntities(world: World, entityFilter: Predicate<Entity>) {
        val fullBoundingBox = Box(startX, startY, startZ, endX, endY, endZ)
        val radius = 2.0 // Vanilla hard-codes 2.0, forge had a custom patch that would allow mods to increase this.
        visitedEntityChunks.clear()
        // iterate the chunks four times, skipping any duplicates. This should eliminate cases where an entity's
        // bounding box extends outside its chunk
        castEntityChunks(world, fullBoundingBox, 0.0, radius, entityFilter)
        castEntityChunks(world, fullBoundingBox, radius, 0.0, entityFilter)
        castEntityChunks(world, fullBoundingBox, 0.0, -radius, entityFilter)
        castEntityChunks(world, fullBoundingBox, -radius, 0.0, entityFilter)
    }

    /**
     * Cast against the entities in the chunks the ray passes through. For purposes of iterating chunks, the ray is
     * offset by [offsetX] and [offsetZ]. This method is called with several different offsets, ensuring that entities
     * that slightly overlap their chunk boundaries are still caught and tested against. Any chunks that have already
     * been tested (according to [visitedEntityChunks]) will be skipped.
     *
     * As an additional optimization, the chunk iteration will short-circuit two chunks after the chunk itself is
     * farther than the current hit fraction. It short-circuits two chunks later because if the ray is passing near the
     * corner of three chunks, then either the adjacent chunk (the first one where distance > fraction) or the diagonal
     * chunk (the one after that) may contain entities that dwarf the current hit. After that point however, no
     * entities will ever be any closer.
     *
     * Granted, iterator distances may be slightly off due to the passed offset, but in every case that an iterator
     * being slightly early makes a chunk worth of difference, the hit is necessarily going to be on the leading edge
     * of the chunk, so nothing will be able to dwarf it from more than two chunks away.
     */
    private fun castEntityChunks(world: World, boundingBox: Box, offsetX: Double, offsetZ: Double, entityFilter: Predicate<Entity>) {
        intersectingIterator.reset(
            (startX + offsetX) / 16, 0.0, (startZ + offsetZ) / 16,
            (endX + offsetX) / 16, 0.0, (endZ + offsetZ) / 16
        )

        var shortCircuitCountdown = -1
        for (chunkPos in intersectingIterator) {
            if (shortCircuitCountdown < 0 && chunkPos.entryFraction > fraction)
                shortCircuitCountdown = 2
            if (shortCircuitCountdown-- == 0)
                break

            if (visitedEntityChunks.add(ChunkPos.toLong(chunkPos.x, chunkPos.z))) {
                val chunk = world.chunkManager.getWorldChunk(chunkPos.x, chunkPos.z, false) ?: continue
                entityList.clear()
                chunk.collectEntities(null, boundingBox, entityList, entityFilter)
                for (i in entityList.indices) {
                    castEntity(entityList[i])
                }
            }
        }
    }

    /**
     * Cast against a single entity
     */
    private fun castEntity(entity: Entity) {
        val box = entity.boundingBox
        if (raycaster.cast(
                true,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                startX, startY, startZ,
                invVelX, invVelY, invVelZ
            )) {
            fraction = raycaster.distance
            normalX = raycaster.normalX
            normalY = raycaster.normalY
            normalZ = raycaster.normalZ

            blockX = 0
            blockY = 0
            blockZ = 0
            this.entity = entity
            hitType = HitType.ENTITY
        }
    }
}

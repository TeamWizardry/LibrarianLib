package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.etcetera.mixin.WorldEntityLookupMixin
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.fluid.FluidState
import net.minecraft.util.TypeFilter
import net.minecraft.util.math.Box
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import java.util.function.Function
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
     * The depth of the hit. This is the distance from the entrance to the exit point, expressed as a multiple of the
     * ray's length, or zero if no impact occurred.
     */
    public var depth: Double = 0.0
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
     * @param shapeContext The context to use when getting block shapes. Generally [ShapeContext.of(entity)][ShapeContext.of].
     *  Defaults to [ShapeContext.absent] if null
     *
     * @see hitType
     * @see blockX
     * @see blockY
     * @see blockZ
     * @see entity
     * @see fraction
     * @see depth
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
        shapeContext: ShapeContext?,
        startX: Double, startY: Double, startZ: Double,
        endX: Double, endY: Double, endZ: Double
    ) {
        val request = RaycastRequest(world, startX, startY, startZ, endX, endY, endZ)
            .withBlockMode(blockMode)
        if (shapeContext != null) {
            request.withShapeContext(shapeContext)
        }
        cast(request)
    }

    /**
     * Cast the ray through the passed world, colliding with blocks and fluids using the specified modes, and colliding
     * with entities according to the specified filter, if present. Passing null for both [entityFilter] and
     * [entityPredicate] will ignore entities.
     *
     * The result of the raycast is made available as properties of this raycaster. It is ***vitally*** important that
     * you call [reset] once you're done with the result to prepare for the next raycast and avoid leaking [entity].
     *
     * @param blockMode The type of collisions to make with solid blocks
     * @param fluidMode The type of collisions to make with fluids
     * @param shapeContext The context to use when getting block shapes. Generally [ShapeContext.of(entity)][ShapeContext.of].
     *  Defaults to [ShapeContext.absent] if null
     * @param entityFilter If non-null, a filter dictating which entity types to collide against. Using this will result
     *  in better performance than using an equivalent [entityPredicate]
     * @param entityPredicate If non-null, a predicate dictating which entities to collide against.
     *
     * @see hitType
     * @see blockX
     * @see blockY
     * @see blockZ
     * @see entity
     * @see fraction
     * @see depth
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
        shapeContext: ShapeContext?,
        entityFilter: TypeFilter<Entity, Entity>?,
        entityPredicate: Predicate<Entity>?,
        startX: Double, startY: Double, startZ: Double,
        endX: Double, endY: Double, endZ: Double
    ) {
        val request = RaycastRequest(world, startX, startY, startZ, endX, endY, endZ)
            .withBlockMode(blockMode)
            .withFluidMode(fluidMode)
        if (shapeContext != null) {
            request.withShapeContext(shapeContext)
        }
        if (entityFilter != null || entityPredicate != null) {
            request.withEntities(entityFilter, entityPredicate)
        }
        return cast(request)
    }

    /**
     * Cast the ray through the configured world, colliding with blocks and fluids using the configured modes, and
     * colliding with entities according to the specified filter, if enabled.
     *
     * The result of the raycast is made available as properties of this raycaster. It is ***vitally*** important that
     * you call [reset] once you're done with the result to prepare for the next raycast and avoid leaking [entity].
     *
     * @see hitType
     * @see blockX
     * @see blockY
     * @see blockZ
     * @see entity
     * @see fraction
     * @see depth
     * @see hitX
     * @see hitY
     * @see hitZ
     * @see normalX
     * @see normalY
     * @see normalZ
     */
    public fun cast(request: RaycastRequest) {
        reset()
        this.startX = request.startX
        this.startY = request.startY
        this.startZ = request.startZ

        this.endX = request.endX
        this.endY = request.endY
        this.endZ = request.endZ

        invVelX = 1.0 / (endX - startX)
        invVelY = 1.0 / (endY - startY)
        invVelZ = 1.0 / (endZ - startZ)

        if (request.blockMode != BlockMode.NONE || request.fluidMode != FluidMode.NONE) {
            castBlocks(
                request.world,
                request.shapeContext,
                request.blockMode,
                request.fluidMode,
                request.blockOverride,
                request.fluidOverride
            )
        }
        if (request.castEntities) {
            castEntities(request.world, request.entityFilter, request.entityPredicate)
        }
    }

    /**
     * Resets the state
     */
    public fun reset() {
        hitType = HitType.NONE
        fraction = 1.0
        depth = 0.0
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

    public class RaycastRequest(
        public val world: World,
        public val startX: Double, public val startY: Double, public val startZ: Double,
        public val endX: Double, public val endY: Double, public val endZ: Double
    ) {
        public var shapeContext: ShapeContext = ShapeContext.absent()

        public var blockMode: BlockMode = BlockMode.NONE
        public var fluidMode: FluidMode = FluidMode.NONE
        public var castEntities: Boolean = false
        public var entityFilter: TypeFilter<Entity, Entity>? = null
        public var entityPredicate: Predicate<Entity>? = null

        public var blockOverride: ShapeOverride<BlockState>? = null
        public var fluidOverride: ShapeOverride<FluidState>? = null

        /**
         * Sets the shape context. Generally [ShapeContext.of(entity)][ShapeContext.of].
         *
         * The default shape context is [ShapeContext.absent]
         */
        public fun withShapeContext(context: ShapeContext): RaycastRequest = apply { this.shapeContext = context }

        /**
         * Sets the shape context using [ShapeContext.of(entity)][ShapeContext.of].
         */
        public fun withEntityContext(entity: Entity): RaycastRequest = withShapeContext(ShapeContext.of(entity))

        /**
         * Sets the type of collisions to make with blocks.
         *
         * The default block mode is [BlockMode.NONE]
         */
        public fun withBlockMode(mode: BlockMode): RaycastRequest = apply { this.blockMode = mode }

        /**
         * Sets the block shape override callback. This will take effect even when [blockMode] is [BlockMode.NONE].
         *
         * The function receives a BlockState parameter and returns a nullable [VoxelShape]. A null return value falls
         * back to the default behavior according to the [blockMode]. To ignore a block return
         * [VoxelShapes.empty()][VoxelShapes.empty].
         */
        public fun withBlockOverride(blockOverride: ShapeOverride<BlockState>): RaycastRequest = apply {
            this.blockOverride = blockOverride
        }

        /**
         * Sets the type of collisions to make with fluids.
         *
         * The default fluid mode is [FluidMode.NONE]
         */
        public fun withFluidMode(mode: FluidMode): RaycastRequest = apply { this.fluidMode = mode }

        /**
         * Sets the fluid shape override callback. This will take effect even when [fluidMode] is [FluidMode.NONE].
         *
         * The function receives a FluidState parameter and returns a nullable [VoxelShape]. A null return value falls
         * back to the default behavior according to the [fluidMode]. To ignore a fluid return
         * [VoxelShapes.empty()][VoxelShapes.empty].
         */
        public fun withFluidOverride(fluidOverride: ShapeOverride<FluidState>): RaycastRequest = apply {
            this.fluidOverride = fluidOverride
        }

        /**
         * Enables entity raycasting and sets the entity filter/predicate. Setting both the filter and the predicate to
         * null will cast against all entities.
         *
         * @param entityFilter If non-null, a filter dictating which entity types to collide against. Using this will
         *  result in better performance than using an equivalent [entityPredicate]
         * @param entityPredicate If non-null, a predicate dictating which entities to collide against.
         */
        public fun withEntities(
            entityFilter: TypeFilter<Entity, Entity>?,
            entityPredicate: Predicate<Entity>?
        ): RaycastRequest = apply {
            this.castEntities = true
            this.entityFilter = entityFilter
            this.entityPredicate = entityPredicate
        }
    }

    public fun interface ShapeOverride<T> {
        /**
         * Returns the shape for the given state or returns null to fall back to the default behavior. To ignore a block
         * return [VoxelShapes.empty].
         *
         * If you modify [pos] (which you should if you need a [BlockPos], to minimize object allocations), you *must*
         * return it to its original value before returning from this function.
         */
        public fun getShape(state: T, world: World, pos: BlockPos.Mutable): VoxelShape?
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
    private val boundingBoxSegmenter = RayBoundingBoxSegmenter()

    // v-------------------------------- Blocks -------------------------------v
    private val mutablePos = BlockPos.Mutable()

    private var invVelX: Double = 0.0
    private var invVelY: Double = 0.0
    private var invVelZ: Double = 0.0

    /**
     * The implementation of block raycasting.
     */
    private fun castBlocks(
        world: World,
        shapeContext: ShapeContext,
        blockMode: BlockMode,
        fluidMode: FluidMode,
        blockOverride: ShapeOverride<BlockState>?,
        fluidOverride: ShapeOverride<FluidState>?
    ) {
        // Only blocks the ray directly passes through are checked.
        intersectingIterator.reset(
            startX, startY, startZ,
            endX, endY, endZ
        )
        for (block in intersectingIterator) {
            if (
                castBlock(
                    world, shapeContext, blockMode,
                    fluidMode, blockOverride, fluidOverride,
                    block.x, block.y, block.z
                )
            ) {
                break // short-circuit at the first hit since we iterate near to far
            }
        }
    }

    /**
     * Cast the ray through the passed block.
     * @return true if the block was hit
     */
    private fun castBlock(
        world: World,
        shapeContext: ShapeContext,
        blockMode: BlockMode,
        fluidMode: FluidMode,
        blockOverride: ShapeOverride<BlockState>?,
        fluidOverride: ShapeOverride<FluidState>?,
        blockX: Int,
        blockY: Int,
        blockZ: Int
    ): Boolean {
        mutablePos.set(blockX, blockY, blockZ)
        val blockShape = when (blockMode) {
            BlockMode.NONE -> {
                // if `blockOverride` is null `world.getBlockState()` is never executed
                blockOverride?.getShape(world.getBlockState(mutablePos), world, mutablePos)
            }
            BlockMode.COLLISION -> {
                val state = world.getBlockState(mutablePos)
                blockOverride?.getShape(state, world, mutablePos)
                    ?: state.getCollisionShape(world, mutablePos, shapeContext)
            }
            BlockMode.VISUAL -> {
                val state = world.getBlockState(mutablePos)
                blockOverride?.getShape(state, world, mutablePos)
                    ?: state.getOutlineShape(world, mutablePos, shapeContext)
            }
        }
        val hitBlock = if(blockShape != null) {
            castShape(blockX, blockY, blockZ, blockShape)
        } else {
            false
        }
        if (hitBlock) {
            entity = null
            this.blockX = blockX
            this.blockY = blockY
            this.blockZ = blockZ
            hitType = HitType.BLOCK
        }

        val fluidShape = when (fluidMode) {
            FluidMode.NONE -> {
                fluidOverride?.getShape(world.getFluidState(mutablePos), world, mutablePos)
            }
            FluidMode.SOURCE -> {
                val state = world.getFluidState(mutablePos)
                fluidOverride?.getShape(state, world, mutablePos)
                    ?: (if (state.isStill) state.getShape(world, mutablePos) else null)
            }
            FluidMode.ANY -> {
                val state = world.getFluidState(mutablePos)
                fluidOverride?.getShape(state, world, mutablePos)
                    ?: state.getShape(world, mutablePos)
            }
        }
        val hitFluid = if(fluidShape != null) {
            castShape(blockX, blockY, blockZ, fluidShape)
        } else {
            false
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
            )
        ) {
            fraction = raycaster.distance
            depth = raycaster.depth
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

    /**
     * The implementation of entity raycasting. This checks against entities on a per-chunk basis
     */
    private fun castEntities(
        world: World,
        entityFilter: TypeFilter<Entity, Entity>?,
        entityPredicate: Predicate<Entity>?
    ) {
        boundingBoxSegmenter.reset(startX, startY, startZ, endX, endY, endZ, 32.0)

        val lookup = mixinCast<WorldEntityLookupMixin>(world).callGetEntityLookup()
        if (entityFilter == null) {
            for (segment in boundingBoxSegmenter) {
                lookup.forEachIntersects(
                    Box(
                        segment.minX, segment.minY, segment.minZ,
                        segment.maxX, segment.maxY, segment.maxZ
                    )
                ) {
                    if (entityPredicate == null || entityPredicate.test(it)) {
                        castEntity(it)
                    }
                }
            }
        } else {
            for (segment in boundingBoxSegmenter) {
                lookup.forEachIntersects(
                    entityFilter,
                    Box(
                        segment.minX, segment.minY, segment.minZ,
                        segment.maxX, segment.maxY, segment.maxZ
                    )
                ) {
                    if (entityPredicate == null || entityPredicate.test(it)) {
                        castEntity(it)
                    }
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
            )
        ) {
            fraction = raycaster.distance
            depth = raycaster.depth
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

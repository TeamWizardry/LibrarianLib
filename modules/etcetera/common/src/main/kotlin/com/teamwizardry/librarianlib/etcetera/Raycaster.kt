package com.teamwizardry.librarianlib.etcetera

import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.etcetera.mixin.WorldEntityLookupMixin
import dev.ryanhcode.sable.companion.SableCompanion
import dev.ryanhcode.sable.companion.SubLevelAccess
import dev.ryanhcode.sable.companion.math.BoundingBox3d
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.Entity
import net.minecraft.fluid.FluidState
import net.minecraft.util.TypeFilter
import net.minecraft.util.function.LazyIterationConsumer
import net.minecraft.util.math.Box
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.World
import org.joml.Vector3d
import org.joml.Vector3dc
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

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
     * The sublevel the raycast hit in
     */
    public var sublevel: SubLevelAccess? = null

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
     * The impacted block's position, or (0,0,0) if no impact occurred. The underlying BlockPos is mutable, and will
     * be updated when another raycast is performed.
     */
    public val block: BlockPos get() = hitBlock

    /**
     * The hit position, or the end position if no hit occurred.
     * This is always in the global coordinate space, even when the hit is in a sublevel.
     */
    public val hit: Vector3dc get() = globalHitPos

    /**
     * The impacted face's normal, or (0,0,0) if no impact occurred.
     * This is always in the global coordinate space, even when the hit is in a sublevel.
     */
    public val normal: Vector3dc get() = globalHitNormal

    /**
     * The ray start position.
     * This is always in the global coordinate space, even when the hit is in a sublevel.
     */
    public val start: Vector3dc get() = globalStartPos

    /**
     * The ray start position.
     * This is always in the global coordinate space, even when the hit is in a sublevel.
     */
    public val end: Vector3dc get() = globalEndPos

    /**
     * The hit position, or the end position if no hit occurred.
     * If [sublevel] is not null, this is in the sublevel's local space.
     */
    public val localHit: Vector3dc get() = localHitPos

    /**
     * The impacted face's normal, or (0,0,0) if no impact occurred.
     * If [sublevel] is not null, this is in the sublevel's local space.
     */
    public val localNormal: Vector3dc get() = localHitNormal

    /**
     * The ray start position.
     * If [sublevel] is not null, this is in the sublevel's local space.
     */
    public val localStart: Vector3dc get() = localStartPos

    /**
     * The ray start position.
     * If [sublevel] is not null, this is in the sublevel's local space.
     */
    public val localEnd: Vector3dc get() = localEndPos

    /* region == Individual component accessors (deprecated) == */
    @Deprecated("Use blockPos", replaceWith = ReplaceWith("this.block.x"))
    public val blockX: Int get() = block.x

    @Deprecated("Use blockPos", replaceWith = ReplaceWith("this.block.y"))
    public val blockY: Int get() = block.y

    @Deprecated("Use blockPos", replaceWith = ReplaceWith("this.block.z"))
    public val blockZ: Int get() = block.z

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.hit.x()"))
    public val hitX: Double get() = hit.x()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.hit.y()"))
    public val hitY: Double get() = hit.y()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.hit.z()"))
    public val hitZ: Double get() = hit.z()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.normal.x()"))
    public val normalX: Double get() = normal.x()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.normal.y()"))
    public val normalY: Double get() = normal.y()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.normal.z()"))
    public val normalZ: Double get() = normal.z()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.start.x()"))
    public val startX: Double get() = start.x()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.start.y()"))
    public val startY: Double get() = start.y()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.start.z()"))
    public val startZ: Double get() = start.z()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.end.x()"))
    public val endX: Double get() = end.x()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.end.y()"))
    public val endY: Double get() = end.y()

    @Deprecated("Use vector", replaceWith = ReplaceWith("this.end.z()"))
    public val endZ: Double get() = end.z()
    /* endregion == Individual component accessors (deprecated) == */

    // populated by raycasts
    private val hitBlock = BlockPos.Mutable()
    private val localHitNormal = Vector3d()

    // computed at the end of the cast
    private val globalStartPos = Vector3d()
    private val globalEndPos = Vector3d()
    private val globalHitPos = Vector3d()
    private val localStartPos = Vector3d()
    private val localEndPos = Vector3d()
    private val localHitPos = Vector3d()
    private val globalHitNormal = Vector3d()

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

        if (request.subLevelMode != SubLevelMode.ONLY_SUBLEVELS) {
            castLevel(null, request)
        }

        if (request.subLevelMode != SubLevelMode.NONE) {
            val sublevels = SableCompanion.INSTANCE.getAllIntersecting(
                request.world,
                BoundingBox3d(
                    min(request.startX, request.endX),
                    min(request.startY, request.endY),
                    min(request.startZ, request.endZ),
                    max(request.startX, request.endX),
                    max(request.startY, request.endY),
                    max(request.startZ, request.endZ),
                )
            )

            for (sublevel in sublevels) {
                castLevel(sublevel, request)
            }
        }

        globalStartPos.set(request.start)
        globalEndPos.set(request.end)
        globalHitPos.set(globalEndPos).sub(globalStartPos).mul(fraction).add(globalStartPos)

        val sublevel = sublevel
        if (sublevel == null) {
            localStartPos.set(globalStartPos)
            localEndPos.set(globalEndPos)
            localHitPos.set(globalHitPos)
            globalHitNormal.set(localHitNormal)
        } else {
            val pose = sublevel.logicalPose()
            pose.transformPositionInverse(globalStartPos, localStartPos)
            pose.transformPositionInverse(globalEndPos, localEndPos)
            pose.transformPositionInverse(globalHitPos, localHitPos)
            pose.transformNormal(localHitNormal, globalHitNormal)
        }
    }

    /**
     * Resets the state
     */
    public fun reset() {
        hitType = HitType.NONE
        entity = null
        sublevel = null
        fraction = 1.0
        depth = 0.0

        localHitNormal.set(0.0, 0.0, 0.0)
        hitBlock.set(0, 0, 0)
        globalStartPos.set(0.0, 0.0, 0.0)
        globalEndPos.set(0.0, 0.0, 0.0)
        localStartPos.set(0.0, 0.0, 0.0)
        localEndPos.set(0.0, 0.0, 0.0)
        raycaster.reset()
    }

    public class RaycastRequest(
        public val world: World,
        startX: Double, startY: Double, startZ: Double,
        endX: Double, endY: Double, endZ: Double
    ) {
        public var shapeContext: ShapeContext = ShapeContext.absent()

        public val start: Vector3dc = Vector3d(startX, startY, startZ)
        public val end: Vector3dc = Vector3d(endX, endY, endZ)

        public val startX: Double get() = start.x()
        public val startY: Double get() = start.y()
        public val startZ: Double get() = start.z()

        public val endX: Double get() = end.x()
        public val endY: Double get() = end.y()
        public val endZ: Double get() = end.z()

        public var subLevelMode: SubLevelMode = SubLevelMode.NONE
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
         * Sets the type of collisions to make with Sable sublevels.
         *
         * The default sublevel mode is [SubLevelMode.NONE]
         */
        public fun withSubLevelMode(mode: SubLevelMode): RaycastRequest = apply { this.subLevelMode = mode }

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

    public enum class SubLevelMode {
        /**
         * Ignore sublevels
         */
        NONE,

        /**
         * Include sublevels
         */
        INCLUDE_SUBLEVELS,

        /**
         * Only cast in sublevels
         */
        ONLY_SUBLEVELS;
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

    private fun castLevel(sublevel: SubLevelAccess?, request: RaycastRequest) {
        val start = sublevel?.logicalPose()?.transformPositionInverse(request.start, Vector3d()) ?: request.start
        val end = sublevel?.logicalPose()?.transformPositionInverse(request.end, Vector3d()) ?: request.end
        if (request.blockMode != BlockMode.NONE || request.fluidMode != FluidMode.NONE) {
            castBlocks(
                request.world,
                sublevel,
                start,
                end,
                request.shapeContext,
                request.blockMode,
                request.fluidMode,
                request.blockOverride,
                request.fluidOverride
            )
        }
        if (request.castEntities) {
            castEntities(
                request.world,
                sublevel,
                start,
                end,
                request.entityFilter,
                request.entityPredicate
            )
        }
    }

    private val intersectingIterator = IntersectingBlocksIterator()
    private val raycaster = DirectRaycaster()
    private val boundingBoxSegmenter = RayBoundingBoxSegmenter()

    // v-------------------------------- Blocks -------------------------------v
    private val mutablePos = BlockPos.Mutable()

    /**
     * The implementation of block raycasting.
     */
    private fun castBlocks(
        world: World,
        sublevel: SubLevelAccess?,
        start: Vector3dc,
        end: Vector3dc,
        shapeContext: ShapeContext,
        blockMode: BlockMode,
        fluidMode: FluidMode,
        blockOverride: ShapeOverride<BlockState>?,
        fluidOverride: ShapeOverride<FluidState>?,
    ) {
        inverseLength.set(
            1.0 / (end.x() - start.x()),
            1.0 / (end.y() - start.y()),
            1.0 / (end.z() - start.z())
        )

        // Only blocks the ray directly passes through are checked.
        intersectingIterator.reset(
            start.x(), start.y(), start.z(),
            end.x(), end.y(), end.z()
        )
        for (block in intersectingIterator) {
            if (
                castBlock(
                    world, sublevel, start,
                    shapeContext, blockMode, fluidMode, blockOverride, fluidOverride,
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
        sublevel: SubLevelAccess?,
        start: Vector3dc,
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
        val hitBlock = blockShape != null && castShape(start, blockX, blockY, blockZ, blockShape)
        if (hitBlock) {
            entity = null
            hitType = HitType.BLOCK
            this.hitBlock.set(blockX, blockY, blockZ)
            this.sublevel = sublevel
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
        val hitFluid = fluidShape != null && castShape(start, blockX, blockY, blockZ, fluidShape)
        if (hitFluid) {
            entity = null
            hitType = HitType.FLUID
            this.hitBlock.set(blockX, blockY, blockZ)
            this.sublevel = sublevel
        }
        return hitBlock || hitFluid
    }

    private val inverseLength = Vector3d()

    /**
     * The ray start relative to the block currently being tested. Used by [boxConsumer]
     */
    private val relativeStart = Vector3d()

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
                relativeStart.x, relativeStart.y, relativeStart.z,
                inverseLength.x, inverseLength.y, inverseLength.z,
            )
        ) {
            fraction = raycaster.distance
            depth = raycaster.depth
            localHitNormal.set(raycaster.normalX, raycaster.normalY, raycaster.normalZ)
            didHitShape = true
        }
    }

    /**
     * Cast the ray through the passed shape.
     */
    private fun castShape(start: Vector3dc, blockX: Int, blockY: Int, blockZ: Int, shape: VoxelShape): Boolean {
        if (shape === VoxelShapes.empty())
            return false

        // the bounding boxes that get fed to [boxConsumer] are all relative to the block (they aren't in absolute world
        // coordinates), so we have to transform the start point to be relative to the block.
        relativeStart.set(start).sub(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())
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
        sublevel: SubLevelAccess?,
        start: Vector3dc,
        end: Vector3dc,
        entityFilter: TypeFilter<Entity, Entity>?,
        entityPredicate: Predicate<Entity>?
    ) {
        inverseLength.set(
            1.0 / (end.x() - start.x()),
            1.0 / (end.y() - start.y()),
            1.0 / (end.z() - start.z())
        )
        boundingBoxSegmenter.reset(start.x(), start.y(), start.z(), end.x(), end.y(), end.z(), 32.0)

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
                        castEntity(sublevel, start, it)
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
                    ),
                    LazyIterationConsumer.forConsumer {
                        if (entityPredicate == null || entityPredicate.test(it)) {
                            castEntity(sublevel, start, it)
                        }
                    }
                )
            }
        }
    }

    /**
     * Cast against a single entity
     */
    private fun castEntity(sublevel: SubLevelAccess?, start: Vector3dc, entity: Entity) {
        val box = entity.boundingBox
        if (raycaster.cast(
                true,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                start.x(), start.y(), start.z(),
                inverseLength.x(), inverseLength.y(), inverseLength.z(),
            )
        ) {
            fraction = raycaster.distance
            depth = raycaster.depth
            localHitNormal.set(raycaster.normalX, raycaster.normalY, raycaster.normalZ)
            hitBlock.set(0, 0, 0)

            this.entity = entity
            hitType = HitType.ENTITY
            this.sublevel = sublevel
        }
    }
}

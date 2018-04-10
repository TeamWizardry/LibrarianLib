package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.features.helpers.addCollisionBoxToList
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * @author WireSegal
 * Created at 9:36 AM on 4/10/18.
 */
interface IMultipleComponentGeometry {
    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        private val renderBoxes = mutableSetOf<AxisAlignedBB>()

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        fun onDrawBlockHighlight(event: DrawBlockHighlightEvent) {
            if (event.target != null && event.target.typeOfHit == RayTraceResult.Type.BLOCK) {
                val player = event.player
                val pos = event.target.blockPos
                val state = player.world.getBlockState(pos)

                if (state.block is IMultipleComponentGeometry) {
                    val iface = state.block as IMultipleComponentGeometry
                    renderBoxes.clear()

                    val x = pos.x
                    val y = pos.y
                    val z = pos.z
                    val hitVec = event.target.hitVec.subtract(x.toDouble(), y.toDouble(), z.toDouble())

                    iface.getSelectionBoxes(state, player.world, pos, hitVec, renderBoxes)

                    if (!renderBoxes.isEmpty())
                        drawSelectionBoxes(renderBoxes, event.player, pos, event.partialTicks)

                    event.isCanceled = true
                }
            }
        }

        @SideOnly(Side.CLIENT)
        private fun drawSelectionBoxes(boxes: Set<AxisAlignedBB>, entity: Entity, pos: BlockPos, ticks: Float) {
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.glLineWidth(2.0f)
            GlStateManager.disableTexture2D()
            GlStateManager.depthMask(false)

            val x = pos.x - (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ticks)
            val y = pos.y - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ticks)
            val z = pos.z - (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ticks)

            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer

            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)

            for (box in boxes) {
                val minX = box.minX - 0.002 + x
                val minY = box.minY - 0.002 + y
                val minZ = box.minZ - 0.002 + z
                val maxX = box.maxX + 0.002 + x
                val maxY = box.maxY + 0.002 + y
                val maxZ = box.maxZ + 0.002 + z
                RenderGlobal.drawBoundingBox(buffer, minX, minY, minZ, maxX, maxY, maxZ, 0.0f, 0.0f, 0.0f, 0.4f)
            }

            tessellator.draw()

            GlStateManager.depthMask(true)
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
        }

        // Use this in your implementation of Block#collisionRayTrace
        fun applyCollisionRayTrace(iface: IMultipleComponentGeometry, state: IBlockState, world: IBlockAccess, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
            val boxes = mutableSetOf<AxisAlignedBB>()
            val results = mutableSetOf<RayTraceResult>()

            iface.getTraceBoxes(state, world, pos, boxes, start, end)

            val x = pos.x.toDouble()
            val y = pos.y.toDouble()
            val z = pos.z.toDouble()
            val a = start.subtract(x, y, z)
            val b = end.subtract(x, y, z)

            for (box in boxes) {
                val result = box.calculateIntercept(a, b)
                if (result != null) {
                    val vec = result.hitVec.addVector(x, y, z)
                    results.add(RayTraceResult(vec, result.sideHit, pos))
                }
            }

            var collidedTrace: RayTraceResult? = null
            var sqrDis = 0.0

            for (result in results) {
                val newSqrDis = result.hitVec.squareDistanceTo(end)
                if (newSqrDis > sqrDis) {
                    collidedTrace = result
                    sqrDis = newSqrDis
                }
            }

            return collidedTrace
        }

        private val tempBoxSet = mutableSetOf<AxisAlignedBB>()

        // Use this in your implementation of Block#addCollisionBoxToList
        fun addCollisionBoxes(iface: IMultipleComponentGeometry, state: IBlockState, world: IBlockAccess, pos: BlockPos, entityBox: AxisAlignedBB, boxes: MutableList<AxisAlignedBB>, isActualState: Boolean) {
            val testState = if (!isActualState) state.getActualState(world, pos) else state
            tempBoxSet.clear()
            iface.getBoundingBoxes(testState, world, pos, tempBoxSet)
            for (box in tempBoxSet)
                addCollisionBoxToList(pos, entityBox, boxes, box)
        }
    }



    fun getBoundingBoxes(state: IBlockState, world: IBlockAccess, pos: BlockPos, boxes: MutableSet<AxisAlignedBB>)

    fun getTraceBoxes(state: IBlockState, world: IBlockAccess, pos: BlockPos, boxes: MutableSet<AxisAlignedBB>, start: Vec3d, end: Vec3d)
            = getBoundingBoxes(state, world, pos, boxes)

    @SideOnly(Side.CLIENT)
    fun getSelectionBoxes(state: IBlockState, world: IBlockAccess, pos: BlockPos, hitVec: Vec3d, boxes: MutableSet<AxisAlignedBB>)
            = getBoundingBoxes(state, world, pos, boxes)
}

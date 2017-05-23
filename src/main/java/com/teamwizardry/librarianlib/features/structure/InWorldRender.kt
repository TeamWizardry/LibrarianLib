package com.teamwizardry.librarianlib.features.structure

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.util.Mirror
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

// TODO : Conversion is lazy, may want to rewrite
@SideOnly(Side.CLIENT)
object InWorldRender {

    private var pos: BlockPos? = null
    private var rot: Rotation? = null
    var structure: Structure? = null
    var verts: IntArray? = null
    var match: StructureMatchResult? = null

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun worldLast(event: RenderWorldLastEvent) {
        if (verts == null)
            return

        GlStateManager.pushAttrib()
        GlStateManager.pushMatrix()

        GlStateManager.enableBlend()

        val player = Minecraft.getMinecraft().player
        val x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks
        val y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks
        val z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks
        GlStateManager.translate(-x, -y, -z)

        GlStateManager.translate(pos!!.x.toFloat(), pos!!.y.toFloat(), pos!!.z.toFloat())

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
        vb.addVertexData(verts!!)
        tessellator.draw()

        GlStateManager.popMatrix()
        GlStateManager.popAttrib()
    }

    @SubscribeEvent
    fun blockPlace(event: BlockEvent.PlaceEvent) {
        if (match == null)
            return
        val pos = worldToStructure(event.pos)
        if (match!!.allErrors.contains(pos))
            refreshVerts()
    }

    @SubscribeEvent
    fun blockBreak(event: BlockEvent.BreakEvent) {
        if (match == null)
            return
        val pos = worldToStructure(event.pos)
        if (match!!.matches.contains(pos) || match!!.allErrors.contains(pos))
            refreshVerts()
    }

    private fun worldToStructure(pos: BlockPos): BlockPos {
        return inverseTransformedBlockPos(pos.subtract(this.pos!!), Mirror.NONE, match!!.rotation).add(structure!!.origin)
    }

    fun unsetStructure() {
        structure = null
        pos = null
        verts = null
        match = null
    }

    fun setStructure(structure: Structure, pos: BlockPos) {
        this.structure = structure
        this.pos = pos
        this.refreshVerts()
    }

    fun refreshVerts() {
        val struct = structure ?: return
        val match = struct.match(Minecraft.getMinecraft().world, pos!!)
        this.match = match
        rot = struct.matchedRotation
        struct.blockAccess.setBlockState(struct.origin, Blocks.AIR.defaultState)

        for (pos in match?.matches ?: listOf<BlockPos>()) {
            struct.blockAccess.setBlockState(pos, Blocks.AIR.defaultState)
        }
        for (pos in match?.nonAirErrors ?: listOf<BlockPos>()) {
            struct.blockAccess.setBlockState(pos, Blocks.AIR.defaultState)
        }
        for (pos in match?.propertyErrors ?: listOf<BlockPos>()) {
            struct.blockAccess.setBlockState(pos, Blocks.AIR.defaultState)
        }

        //		verts = StructureRenderUtil.render(structure, (check) -> true, (check) -> EnumFacing.values(), new Color(1, 1, 1, 0.75f), 0.75f);

        structure!!.blockAccess.resetSetBlocks()
    }


    private fun transformedBlockPos(pos: BlockPos, mirrorIn: Mirror, rotationIn: Rotation): BlockPos {
        var i = pos.x
        val j = pos.y
        var k = pos.z
        var flag = true

        when (mirrorIn) {
            Mirror.LEFT_RIGHT -> k = -k
            Mirror.FRONT_BACK -> i = -i
            else -> flag = false
        }

        when (rotationIn) {
            Rotation.COUNTERCLOCKWISE_90 -> return BlockPos(k, j, -i)
            Rotation.CLOCKWISE_90 -> return BlockPos(-k, j, i)
            Rotation.CLOCKWISE_180 -> return BlockPos(-i, j, -k)
            else -> return if (flag) BlockPos(i, j, k) else pos
        }
    }

    private fun inverseTransformedBlockPos(pos: BlockPos, mirrorIn: Mirror, rotationIn: Rotation): BlockPos {
        var i = pos.x
        val j = pos.y
        var k = pos.z

        if (rotationIn == Rotation.COUNTERCLOCKWISE_90) {
            i = -i
            k = -k
            i = -i
            k = -k
        } else if (rotationIn == Rotation.CLOCKWISE_90) {
            k = -k
            i = -i
            k = -k
        } else if (rotationIn == Rotation.CLOCKWISE_180) {
            i = -i
            k = -k
        }


        if (mirrorIn == Mirror.LEFT_RIGHT) k = -k
        else if (mirrorIn == Mirror.FRONT_BACK) i = -i

        return BlockPos(i, j, k)
    }
}

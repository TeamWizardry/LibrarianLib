package com.teamwizardry.librarianlib.features.structure.dynamic

import com.teamwizardry.librarianlib.features.kotlin.renderPosX
import com.teamwizardry.librarianlib.features.kotlin.renderPosY
import com.teamwizardry.librarianlib.features.kotlin.renderPosZ
import io.netty.util.collection.LongObjectHashMap
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * @author WireSegal
 * Created at 4:43 PM on 3/29/18.
 */
class DynamicStructure(inheriting: LongObjectHashMap<DynamicBlockInfo>) {
    private val packed = LongObjectHashMap<DynamicBlockInfo>().apply {
        putAll(inheriting)
    }

    fun composePosition(origin: BlockPos, pack: Long, orientation: EnumFacing): Long {
        val px = fromLongX(pack)
        val py = fromLongY(pack)
        val pz = fromLongZ(pack)
        val ox = origin.x
        val oy = origin.y
        val oz = origin.z
        val dr = orientation.axisDirection.offset

        if (orientation.axis == EnumFacing.Axis.X)
            return toLong(ox + pz * -dr, oy + py, oz + px * dr)
        return toLong(ox + px * -dr, oy + py, oz + py * -dr)
    }

    fun match(world: IBlockAccess, origin: BlockPos, orientation: EnumFacing): Boolean {
        val mutable = BlockPos.MutableBlockPos()
        for ((pack, info) in packed) {
            val composed = composePosition(origin, pack, orientation)
            mutable.setPos(fromLongX(composed), fromLongY(composed), fromLongZ(composed))
            if (!info(world, mutable, orientation))
                return false
        }

        return true
    }

    fun match(world: IBlockAccess, origin: BlockPos): EnumFacing?
            = EnumFacing.HORIZONTALS.firstOrNull { match(world, origin, it) }

    @JvmOverloads
    @SideOnly(Side.CLIENT)
    fun render(world: IBlockAccess, origin: BlockPos, orientation: EnumFacing, ignoreExisting: Boolean = true, timeSinceBegan: Int = 0) {
        val mutable = BlockPos.MutableBlockPos()

        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)

        val dispatcher = Minecraft.getMinecraft().blockRendererDispatcher

        access.packed.clear()


        for ((pack, info) in packed) {
            val composed = composePosition(origin, pack, orientation)
            mutable.setPos(fromLongX(composed), fromLongY(composed), fromLongZ(composed))
            if (!ignoreExisting || !info(world, mutable, orientation)) {
                val valid = info.validStates
                val state = valid[(timeSinceBegan / 20) % valid.size]
                access.packed[pack] = state
            }
        }

        val renderPosX = Minecraft.getMinecraft().renderManager.renderPosX
        val renderPosY = Minecraft.getMinecraft().renderManager.renderPosY
        val renderPosZ = Minecraft.getMinecraft().renderManager.renderPosZ

        for ((pack, state) in access.packed) {
            mutable.setPos(fromLongX(pack), fromLongY(pack), fromLongZ(pack))
            dispatcher.renderBlock(state, mutable, access, buffer)
        }

        GlStateManager.pushMatrix()
        GlStateManager.translate(-renderPosX, -renderPosY, -renderPosZ)

        GlStateManager.translate(origin.x.toDouble(), origin.y.toDouble(), origin.z.toDouble())

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableDepth()
        GlStateManager.disableLighting()

        GlStateManager.color(1f, 1f, 1f, 0.4f)
        Tessellator.getInstance().draw()
        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableLighting()
        GlStateManager.enableDepth()

        GlStateManager.popMatrix()
    }

    @SideOnly(Side.CLIENT)
    fun renderOnPage(timeSinceBegan: Int = 0) {

        val buffer = Tessellator.getInstance().buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
        val dispatcher = Minecraft.getMinecraft().blockRendererDispatcher

        val mutable = BlockPos.MutableBlockPos()

        for ((pack, info) in packed) {
            val valid = info.validStates
            val state = valid[(timeSinceBegan / 20) % valid.size]
            access.packed[pack] = state
        }

        for ((pack, state) in access.packed) {
            mutable.setPos(fromLongX(pack), fromLongY(pack), fromLongZ(pack))
            dispatcher.renderBlock(state, mutable, access, buffer)
        }

        val renderPosX = Minecraft.getMinecraft().renderManager.renderPosX
        val renderPosY = Minecraft.getMinecraft().renderManager.renderPosY
        val renderPosZ = Minecraft.getMinecraft().renderManager.renderPosZ

        GlStateManager.pushMatrix()
        GlStateManager.translate(-renderPosX, -renderPosY, -renderPosZ)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableLighting()

        GlStateManager.color(1f, 1f, 1f, 0.4f)
        Tessellator.getInstance().draw()
        GlStateManager.color(1f, 1f, 1f, 1f)

        GlStateManager.enableLighting()

        GlStateManager.popMatrix()
    }
}

private val access = StructureBlockAccess()

private val voidBiome by lazy { Biome.REGISTRY.getObjectById(127)!! }

private class StructureBlockAccess : IBlockAccess {

    val packed = LongObjectHashMap<IBlockState>()

    fun isValid(pack: Long) = pack in packed

    override fun isSideSolid(pos: BlockPos, side: EnumFacing, defaultTo: Boolean): Boolean {
        val pack = pos.toLong()
        if (!isValid(pack)) return defaultTo
        val state = getBlockState(pack)
        return state.isSideSolid(this, pos, side)
    }

    override fun isAirBlock(pos: BlockPos): Boolean {
        val state = getBlockState(pos)
        return state.block.isAir(state, this, pos)
    }

    override fun getStrongPower(pos: BlockPos, direction: EnumFacing) = 0

    override fun getCombinedLight(pos: BlockPos, lightValue: Int) = 0xf000f0

    override fun getTileEntity(pos: BlockPos): TileEntity? = null

    fun getBlockState(pack: Long): IBlockState = packed.getOrDefault(pack, Blocks.AIR.defaultState)

    override fun getBlockState(pos: BlockPos) = getBlockState(pos.toLong())

    override fun getBiome(pos: BlockPos): Biome = voidBiome

    override fun getWorldType(): WorldType = WorldType.DEFAULT
}

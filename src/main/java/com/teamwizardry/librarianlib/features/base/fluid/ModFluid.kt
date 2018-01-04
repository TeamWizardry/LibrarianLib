package com.teamwizardry.librarianlib.features.base.fluid

import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil

/**
 * @author WireSegal
 * Created at 8:18 AM on 11/23/17.
 */
@Suppress("LeakingThis")
open class ModFluid(name: String, rlStill: ResourceLocation, rlFlow: ResourceLocation, bucket: Boolean) : Fluid(name, rlStill, rlFlow) {

    constructor(name: String, still: String, flow: String, bucket: Boolean) :
            this(name, ResourceLocation(currentModId, still), ResourceLocation(currentModId, flow), bucket)

    private var vaporizes: Boolean? = null
    private var myBlock: Block? = null

    override fun doesVaporize(fluidStack: FluidStack) = vaporizes ?: super.doesVaporize(fluidStack)

    override fun setDensity(density: Int): ModFluid
            = super.setDensity(density) as ModFluid

    override fun setEmptySound(emptySound: SoundEvent?): ModFluid
            = super.setEmptySound(emptySound) as ModFluid

    override fun setViscosity(viscosity: Int): ModFluid
            = super.setViscosity(viscosity) as ModFluid

    override fun setTemperature(temperature: Int): ModFluid
            = super.setTemperature(temperature) as ModFluid

    override fun setGaseous(isGaseous: Boolean): ModFluid
            = super.setGaseous(isGaseous) as ModFluid

    override fun setRarity(rarity: EnumRarity?): ModFluid
            = super.setRarity(rarity) as ModFluid

    override fun setFillSound(fillSound: SoundEvent?): ModFluid
            = super.setFillSound(fillSound) as ModFluid

    override fun setLuminosity(luminosity: Int): ModFluid
            = super.setLuminosity(luminosity) as ModFluid

    open fun makeBlock(material: Material): ModFluid
            = apply { if (getActualBlock() == null) myBlock = BlockModFluid(getActual(), material) }

    open fun setVaporizes(vaporizes: Boolean): ModFluid
            = apply { this.vaporizes = vaporizes }


    fun bucketOf(): ItemStack = FluidUtil.getFilledBucket(FluidStack(getActual(), 1000))

    fun getActual(): Fluid = FluidRegistry.getFluid(name)
    fun getActualBlock(): Block? = getActual().block

    init {
        FluidRegistry.registerFluid(this)
        if (bucket && getActual() == this)
            FluidRegistry.addBucketForFluid(this)
    }
}

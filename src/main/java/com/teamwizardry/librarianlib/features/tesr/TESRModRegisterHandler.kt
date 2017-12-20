package com.teamwizardry.librarianlib.features.tesr

import com.teamwizardry.librarianlib.features.autoregister.AMPRegister
import com.teamwizardry.librarianlib.features.autoregister.AnnotationMarkerProcessor
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraftforge.fml.client.registry.ClientRegistry
import kotlin.reflect.KClass


annotation class TileRenderer(val value: KClass<out TileRenderHandler<TileMod>>, val fast: Boolean = false)


@AMPRegister
object TileRendererRegisterProcessor : AnnotationMarkerProcessor<TileRenderer, TileMod>(TileRenderer::class.java, TileMod::class.java) {

    internal val tileRendererRegisters = mutableMapOf<Class<out TileMod>, Pair<Boolean, (TileMod) -> TileRenderHandler<TileMod>>>()

    override fun process(clazz: Class<TileMod>, annotation: TileRenderer) {
        ClientRunnable.run {
            val renderer = annotation.value.java

            val constructor = renderer.constructors.find {
                it.parameterCount == 1 && it.parameters[0].type.isAssignableFrom(clazz)
            } ?: throw IllegalArgumentException("No valid constructor found. The ${renderer.canonicalName} must have a " +
                    "constructor with a single parameter whose type is a supertype of ${clazz.canonicalName}")

            val mh = MethodHandleHelper.wrapperForConstructor(constructor)
            tileRendererRegisters[clazz] = annotation.fast to { tile -> mh(arrayOf(tile)) as TileRenderHandler<TileMod> }
            if (annotation.fast)
                TileMod.fastTESRClasses.add(clazz)
        }
    }

    fun register() {
        tileRendererRegisters.forEach { tile, (fast, renderer) ->
            ClientRegistry.bindTileEntitySpecialRenderer(tile, TESRMod(renderer, fast))
        }
    }
}

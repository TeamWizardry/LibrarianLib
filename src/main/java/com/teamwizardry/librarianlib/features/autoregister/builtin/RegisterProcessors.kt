package com.teamwizardry.librarianlib.features.autoregister.builtin

import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.autoregister.*
import com.teamwizardry.librarianlib.features.kotlin.singletonInstance
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.FieldType
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by TheCodeWarrior
 */

@AMPRegister
object TileRegisterProcessor : AnnotationMarkerProcessor<TileRegister, TileEntity>(TileRegister::class.java, TileEntity::class.java) {
    override fun process(clazz: Class<TileEntity>, annotation: TileRegister) {
        val name = annotation.value
        val owner = OwnershipHandler.getModId(clazz)
        var loc: ResourceLocation =
                if (name == "") clazz.canonicalName.toLowerCase().toRl()
                else ResourceLocation(name)

        if (loc.resourceDomain == "minecraft" && owner == null)
            throw RuntimeException("No mod id and class has no known owner!")

        if (loc.resourceDomain == "minecraft")
            loc = ResourceLocation(owner, loc.resourcePath)
        AbstractSaveHandler.cacheFields(clazz)
        @Suppress("UNCHECKED_CAST")
        GameRegistry.registerTileEntity(clazz, loc.toString())
    }
}

@AMPRegister
object PacketRegisterProcessor : AnnotationMarkerProcessor<PacketRegister, PacketBase>(PacketRegister::class.java, PacketBase::class.java) {
    override fun process(clazz: Class<PacketBase>, annotation: PacketRegister) {
        val side = annotation.value
        @Suppress("UNCHECKED_CAST")
        PacketHandler.register(clazz, side)
    }
}

@AMPRegister
object SerializerRegisterProcessor : AnnotationMarkerProcessor<SerializerRegister, Serializer<*>>(SerializerRegister::class.java, Serializer::class.java) {
    override fun process(clazz: Class<Serializer<*>>, annotation: SerializerRegister) {
        val classes = annotation.classes
        val instance = clazz.singletonInstance

        if(instance == null) {
            throw RuntimeException("No singleton instance")
        } else {
            classes.forEach {
                SerializerRegistry.register(FieldType.create(it.javaObjectType), instance)
                if(it.javaPrimitiveType != null)
                    SerializerRegistry.register(FieldType.create(it.javaPrimitiveType!!), instance)
            }
        }
    }
}

@AMPRegister
object SerializerFactoryRegisterProcessor : AnnotationMarkerProcessor<SerializerFactoryRegister, SerializerFactory>(SerializerFactoryRegister::class.java, SerializerFactory::class.java) {
    override fun process(clazz: Class<SerializerFactory>, annotation: SerializerFactoryRegister) {
        val instance = clazz.singletonInstance

        if(instance == null) {
            throw RuntimeException("No singleton instance")
        } else {
            SerializerRegistry.register(instance)
        }
    }
}

/*

:s/Void/Yours/g

@AMPRegister
object VoidRegisterProcessor : AnnotationMarkerProcessor<VoidRegister>(VoidRegister::class.java, Void::class.java) {
    override fun process(clazz: Class<Any>, annotation: VoidRegister) {
    }
}

*/

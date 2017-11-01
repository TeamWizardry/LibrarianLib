package com.teamwizardry.librarianlib.features.autoregister.builtin

import com.teamwizardry.librarianlib.core.common.OwnershipHandler
import com.teamwizardry.librarianlib.features.autoregister.*
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.kotlin.singletonInstance
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.properties.ModProperty
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
                if (name == "") VariantHelper.toSnakeCase(clazz.simpleName).toRl()
                else ResourceLocation(name)

        if (loc.resourceDomain == "minecraft" && owner == null)
            throw RuntimeException("No mod id and class has no known owner!")

        if (loc.resourceDomain == "minecraft")
            loc = ResourceLocation(owner, loc.resourcePath)
        AbstractSaveHandler.cacheFields(clazz)
        GameRegistry.registerTileEntity(clazz, loc.toString())
    }
}

@AMPRegister
object PacketRegisterProcessor : AnnotationMarkerProcessor<PacketRegister, PacketBase>(PacketRegister::class.java, PacketBase::class.java) {
    override fun process(clazz: Class<PacketBase>, annotation: PacketRegister) {
        val side = annotation.value
        PacketHandler.register(clazz, side)
    }
}

@AMPRegister
object SerializerRegisterProcessor : AnnotationMarkerProcessor<SerializerRegister, Serializer<*>>(SerializerRegister::class.java, Serializer::class.java) {
    override fun process(clazz: Class<Serializer<*>>, annotation: SerializerRegister) {
        val classes = annotation.classes
        val instance = clazz.singletonInstance

        if(instance == null) throw RuntimeException("No singleton instance")
        else {
            classes.forEach {
                SerializerRegistry.register(FieldType.create(it.javaObjectType, null), instance)
                if(it.javaPrimitiveType != null)
                    SerializerRegistry.register(FieldType.create(it.javaPrimitiveType!!, null), instance)
            }
        }
    }
}

@AMPRegister
object SerializerFactoryRegisterProcessor : AnnotationMarkerProcessor<SerializerFactoryRegister, SerializerFactory>(SerializerFactoryRegister::class.java, SerializerFactory::class.java) {
    override fun process(clazz: Class<SerializerFactory>, annotation: SerializerFactoryRegister) {
        val instance = clazz.singletonInstance

        if(instance == null) throw RuntimeException("No singleton instance")
        else SerializerRegistry.register(instance)
    }
}

@AMPRegister
object PropertyRegisterProcessor : AnnotationMarkerProcessor<PropertyRegister, ModProperty>(PropertyRegister::class.java, ModProperty::class.java) {
    override fun process(clazz: Class<ModProperty>, annotation: PropertyRegister) {
        val instance = clazz.singletonInstance

        if(instance == null) throw RuntimeException("No singleton instance")
        else {
            var name = annotation.value
            if (name.isEmpty())
                name = clazz.simpleName

            ModProperty.registerProperty(name, instance)
        }
    }
}

/*

:s/Void/Yours/g

@AMPRegister
object VoidRegisterProcessor : AnnotationMarkerProcessor<VoidRegister>(VoidRegister::class.java, Void::class.java) {
    override fun process(clazz: Class<Void>, annotation: VoidRegister) {
    }
}

*/

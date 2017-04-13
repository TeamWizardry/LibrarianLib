package com.teamwizardry.librarianlib.test.docsPlayground;

import com.teamwizardry.librarianlib.features.autoregister.SerializerFactoryRegister;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.saving.FieldType;
import com.teamwizardry.librarianlib.features.saving.FieldTypeGeneric;
import com.teamwizardry.librarianlib.features.saving.serializers.Serializer;
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactory;
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerFactoryMatch;
import com.teamwizardry.librarianlib.features.saving.serializers.SerializerRegistry;
import io.netty.buffer.ByteBuf;
import kotlin.Lazy;
import kotlin.jvm.functions.Function1;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

@SerializerFactoryRegister
public class SerializeCellFactory extends SerializerFactory {
	public SerializeCellFactory() {
		super("Cell");
	}
	
	// this method checks to see whether this factory should apply to the passed type, and returns one of a few levels
	// of "specificity" so more specific factories take precedence.
	@NotNull
	@Override
	public SerializerFactoryMatch canApply(@NotNull FieldType type) {
		// In this case, we want to serialize any kind of Cell, including subclasses. In this case we use
		// `canApplySubclass(type, classes...)` which will check if the type is a subclass of any of the passed classes.
		
		// If we wanted to check the exact type we would use `canApplyExact(type, classes...)`.
		
		// If you wanted to "or" both together you could do `canApplyExact(...).or(canApplySubclass(...))`
		
		return this.canApplySubclass(type, Cell.class);
	}
	
	// this method actually creates a serializer for the type. These serializers are the same interface and class
	// structure as your static serializer, but are a whole different beast to deal with. A much more convoluted beast.
	@NotNull
	@Override
	public Serializer<?> create(@NotNull FieldType type) {
		FieldType component = getComponentType(type);
		Function1<Object, Object> constructor = getConstructor(type, component);
		return new SerializeCell(type, component, constructor);
	}
	
	@NotNull
	private FieldType getComponentType(@NotNull FieldType type) {
		// so, a little complex to explain, but here goes:
		// if DuperCell<A, B, C> extends SuperCell<B, C>
		// and SuperCell<A2, B2> extends Cell<B2>
		// and Cell<A3>
		// then it will find the appropriate A3 based on the types of DuperCell. It's complex, I know.
		// The general rule is, if you want to get the types of a Map, getGenericSuperclass(Map.class), same for List, etc.
		// Don't trust the generic types of the passed class, always use genericSuperclass.
		FieldTypeGeneric cellType = (FieldTypeGeneric)type.genericSuperclass(Cell.class);
		
		FieldType valueType = cellType.generic(0); // get the first (and only) type parameter
		
		return valueType;
	}
	
	@NotNull
	private Function1<Object, Object> getConstructor(FieldType type, FieldType componentType) {
		// see above method
		FieldTypeGeneric genericType = (FieldTypeGeneric)type;
		
		// We will search for a constructor that has a single argument that maps to the component type:
		
		Constructor[] constructors = type.getClazz().getDeclaredConstructors();
		
		for(Constructor constructor : constructors) {
			if(constructor.getParameterCount() != 1)
				continue;
			Type paramType = constructor.getGenericParameterTypes()[0];
			FieldType resolvedType = type.resolve(paramType);
			if(resolvedType.equals(componentType)) {
				Function1 wrapper = MethodHandleHelper.wrapperForConstructor(constructor);
				return (component) -> wrapper.invoke(new Object[]{ component });
			}
		}
		
		throw new IllegalArgumentException("Class " + type.toString() + " has no single-argument constructor that matches that of Cell");
	}
	
	
	class SerializeCell extends Serializer<Cell> {
		
		FieldType component;
		Function1<Object, Object> constructor;
		Lazy<Serializer<Object>> componentSerializer;
		
		public SerializeCell(@NotNull FieldType type, @NotNull FieldType component, @NotNull Function1<Object, Object> constructor) {
			super(type);
			
			this.component = component;
			this.constructor = constructor;
			componentSerializer = SerializerRegistry.INSTANCE.lazy(component);
		}
		
		// these methods are bad examples when it comes to using existing values, but when I made them good examples they
		// were very difficult to follow. This is one case where the difference in performance is just about 0, so it's
		// ok to skimp on development time, but for much more complicated stuff you'll want to reduce reinstantiations.
		@NotNull
		@Override
		protected Cell readNBT(@NotNull NBTBase nbt, @Nullable Cell existing, boolean syncing) {
			NBTTagCompound compound = (NBTTagCompound)nbt;
			
			boolean bool = compound.getBoolean("bool");
			NBTBase componentTag = compound.getTag("component");
			Object component = componentTag == null ? null : // if no tag, it's null
				// if there is a tag, deserialize. The existing value is pulled from `existing` if there is an existing cell.
				componentSerializer.getValue().read(componentTag, null, syncing);
			
			Cell returnCell = (Cell) constructor.invoke(component);
			returnCell.bool = bool;
			
			return returnCell;
		}
		
		@NotNull
		@Override
		protected NBTBase writeNBT(@NotNull Cell value, boolean syncing) {
			NBTTagCompound compound = new NBTTagCompound();
			
			compound.setBoolean("bool", value.bool);
			if(value.value != null) // if not null, create an NBT tag and put that in the compound. If it is null the tag
				// won't be present, which will signal the deserializer to set it to null.
				compound.setTag("component", componentSerializer.getValue().write(value.value, syncing));
			
			return compound;
		}
		
		// see comment on readNBT
		@NotNull
		@Override
		protected Cell readBytes(@NotNull ByteBuf buf, @Nullable Cell existing, boolean syncing) {
			
			boolean bool = buf.readBoolean();
			Object component = buf.readBoolean() ? null : // if no tag, it's null
				// if there is a tag, deserialize. The existing value is pulled from `existing` if there is an existing cell.
				componentSerializer.getValue().read(buf, null, syncing);
			
			Cell returnCell = (Cell) constructor.invoke(component);
			returnCell.bool = bool;
			
			return returnCell;
		}
		
		
		// it is super hyper ultra mega critically important that you read and write the EXACT same stuff on the sender
		// and receiver. If you don't things will screw up REALLY BADLY.
		//
		// So in this case I send a boolean so if there is no component to sync I don't "over-read" on the client and
		// read more bytes than I wrote. If I did that, again, VERY BAD STUFF WOULD HAPPEN.
		@Override
		protected void writeBytes(@NotNull ByteBuf buf, @NotNull Cell value, boolean syncing) {
			buf.writeBoolean(value.bool);
			buf.writeBoolean(value.value != null);
			if(value.value != null) {
				componentSerializer.getValue().write(buf, value.value, syncing);
			}
		}
	}
}

/*

object SerializeListFactory : SerializerFactory("List") {
    override fun canApply(type: FieldType): SerializerFactoryMatch {
    }

    override fun create(type: FieldType): Serializer<*> {
        val superclass = type.genericSuperclass(List::class.java) as FieldTypeGeneric
        return SerializeList(type, superclass.generic(0))
    }

    class SerializeList(type: FieldType, val generic: FieldType) : Serializer<MutableList<Any?>>(type) {

        val serGeneric: Serializer<Any> by SerializerRegistry.lazy(generic)
        val constructor = createConstructorMH()

        override fun readNBT(nbt: NBTBase, existing: MutableList<Any?>?, syncing: Boolean): MutableList<Any?> {
            val list = nbt.safeCast(NBTTagList::class.java)

            @Suppress("UNCHECKED_CAST")
            val array = (existing ?: constructor())

            while (array.size > list.tagCount())
                array.removeAt(array.size - 1)

            list.forEachIndexed<NBTTagCompound> { i, container ->
                val tag = container.getTag("-")
                val v = if (tag == null) null else serGeneric.read(tag, array.getOrNull(i), syncing)
                if (i >= array.size) {
                    array.add(v)
                } else {
                    array[i] = v
                }
            }

            return array
        }

        override fun writeNBT(value: MutableList<Any?>, syncing: Boolean): NBTBase {
            val list = NBTTagList()

            for (i in 0..value.size - 1) {
                val container = NBTTagCompound()
                list.appendTag(container)
                val v = value[i]
                if (v != null) {
                    container.setTag("-", serGeneric.write(v, syncing))
                }
            }

            return list
        }

        override fun readBytes(buf: ByteBuf, existing: MutableList<Any?>?, syncing: Boolean): MutableList<Any?> {
            val nullsig = buf.readBooleanArray()

            @Suppress("UNCHECKED_CAST")
            val array = (existing ?: constructor())

            while (array.size > nullsig.size)
                array.removeAt(array.size - 1)

            for (i in 0..nullsig.size - 1) {
                val v = if (nullsig[i]) null else serGeneric.read(buf, array.getOrNull(i), syncing)
                if (i >= array.size) {
                    array.add(v)
                } else {
                    array[i] = v
                }
            }
            return array
        }

        override fun writeBytes(buf: ByteBuf, value: MutableList<Any?>, syncing: Boolean) {
            val nullsig = BooleanArray(value.size) { value[it] == null }
            buf.writeBooleanArray(nullsig)

            (0..value.size - 1)
                    .filterNot { nullsig[it] }
                    .forEach { serGeneric.write(buf, value[it]!!, syncing) }
        }

        private fun createConstructorMH(): () -> MutableList<Any?> {
            if (type.clazz == List::class.java) {
                return { mutableListOf<Any?>() }
            } else {
                val mh =  MethodHandleHelper.wrapperForConstructor<MutableList<Any?>>(type.clazz)
                return { mh(arrayOf()) }
            }
        }
    }
}
 */

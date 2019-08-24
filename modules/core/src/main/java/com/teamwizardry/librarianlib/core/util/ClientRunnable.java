package com.teamwizardry.librarianlib.core.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class provides a lambda that is safe to contain clientside code.
 * Use this instead of proxying a single use method.
 */
@FunctionalInterface
public interface ClientRunnable {
    /**
     * Executes a lambda only if the side is client.
     * <p>
     * Because the SAM of {@link ClientRunnable} is {@link OnlyIn},
     * any {@link OnlyIn} client code is safe to have inside.
     * This only works for Kotlin.
     *
     * @param runnable A {@link ClientRunnable}.
     */
    static void run(ClientRunnable runnable) {
        if(FMLEnvironment.dist.isClient()) {
            runnable.runIfClient();
        }
    }

    /**
     * Executes a lambda that returns a value, but only if the side is client.
     * <p>
     * Because the SAM of {@link ClientSupplier} is {@link OnlyIn},
     * any {@link OnlyIn} client code is safe to have inside.
     * This only works for Kotlin.
     *
     * @param supplier An {@link ClientSupplier} that produces an object of type {@link T}.
     * @param <T>      The type the supplier creates.
     * @return Null if server-side, otherwise the result of the supplier.
     */
    @Nullable
    static <T> T produce(ClientSupplier<T> supplier) {
        MutableObject<T> holder = new MutableObject<>();
        run(new ClientRunnable() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void runIfClient() {
                holder.setValue(supplier.produceIfClient());
            }
        });
        return holder.getValue();
    }

    @OnlyIn(Dist.CLIENT)
    void runIfClient();

    @FunctionalInterface
    interface ClientSupplier<T> {

        @OnlyIn(Dist.CLIENT)
        T produceIfClient();
    }
}


package com.teamwizardry.librarianlib.core.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@FunctionalInterface
public interface SidedSupplier<T> extends Supplier<T> {
    @Nullable
    @Override
    T get();



    @Nullable
    static <T> T client(SidedSupplier.Client<T> supplier) {
        return supplier.get();
    }

    @Nullable
    static <T> T server(SidedSupplier.Server<T> supplier) {
        return supplier.get();
    }

    static <T> T sided(SidedSupplier.Client<T> clientSupplier, SidedSupplier.Server<T> serverSupplier) {
        return new Separate<T>() {
            @Override
            public T getClient() {
                return clientSupplier.getClient();
            }

            @Override
            public T getServer() {
                return serverSupplier.getServer();
            }
        }.get();
    }

    @FunctionalInterface
    interface Client<T> extends SidedSupplier<T> {

        @Nullable
        @Override
        default T get() {
            if(FMLEnvironment.dist.isClient()) {
                return getClient();
            }
            return null;
        }

        @OnlyIn(Dist.CLIENT)
        T getClient();
    }

    @FunctionalInterface
    interface Server<T> extends SidedSupplier<T> {
        @Override
        @Nullable
        default T get() {
            if(FMLEnvironment.dist.isDedicatedServer()) {
                return getServer();
            }
            return null;
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        T getServer();
    }

    interface Separate<T> extends Supplier<T> {
        @Override
        default T get() {
            switch(FMLEnvironment.dist) {
                case CLIENT:
                    return getClient();
                case DEDICATED_SERVER:
                    return getServer();
            }
            return null;
        }

        @OnlyIn(Dist.CLIENT)
        T getClient();

        @OnlyIn(Dist.DEDICATED_SERVER)
        T getServer();
    }
}

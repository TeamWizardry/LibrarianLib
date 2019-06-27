package com.teamwizardry.librarianlib.core.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@FunctionalInterface
public interface SidedFunction<T, R> extends Function<T, R> {

    @Nullable
    @Override
    R apply(T t);

    static <T, R> R client(T argument, SidedFunction.Client<T, R> function) {
        return function.apply(argument);
    }

    @Nullable
    static <T, R> R server(T argument, SidedFunction.Server<T, R> function) {
        return function.apply(argument);
    }

    static <T, R> R sided(T argument, SidedFunction.Client<T, R> clientFunction, SidedFunction.Server<T, R> serverFunction) {
        return new Separate<T, R>() {
            @Override
            public R applyClient(T t) {
                return clientFunction.applyClient(t);
            }

            @Override
            public R applyServer(T t) {
                return serverFunction.applyServer(t);
            }
        }.apply(argument);
    }

    @FunctionalInterface
    interface Client<T, R> extends SidedFunction<T, R> {

        @Nullable
        @Override
        default R apply(T t) {
            if(FMLEnvironment.dist.isClient()) {
                return applyClient(t);
            }
            return null;
        }

        @OnlyIn(Dist.CLIENT)
        R applyClient(T t);
    }

    @FunctionalInterface
    interface Server<T, R> extends SidedFunction<T, R> {
        @Override
        default R apply(T t) {
            if(FMLEnvironment.dist.isDedicatedServer()) {
                return applyServer(t);
            }
            return null;
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        R applyServer(T t);
    }

    interface Separate<T, R> extends Function<T, R> {
        @Override
        default R apply(T t) {
            switch(FMLEnvironment.dist) {
                case CLIENT:
                    return applyClient(t);
                case DEDICATED_SERVER:
                    return applyServer(t);
            }
            return null;
        }

        @OnlyIn(Dist.CLIENT)
        R applyClient(T t);

        @OnlyIn(Dist.DEDICATED_SERVER)
        R applyServer(T t);
    }
}

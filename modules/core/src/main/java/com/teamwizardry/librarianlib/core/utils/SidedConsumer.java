package com.teamwizardry.librarianlib.core.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

@FunctionalInterface
public interface SidedConsumer<T> extends Consumer<T> {

    static <T> void client(T argument, SidedConsumer.Client<T> consumer) {
        consumer.accept(argument);
    }

    static <T> void server(T argument, SidedConsumer.Server<T> consumer) {
        consumer.accept(argument);
    }

    static <T> void sided(T argument, SidedConsumer.Client<T> clientConsumer, SidedConsumer.Server<T> serverConsumer) {
        new Separate<T>() {
            @Override
            public void acceptClient(T t) {
                clientConsumer.acceptClient(t);
            }

            @Override
            public void acceptServer(T t) {
                serverConsumer.acceptServer(t);
            }
        }.accept(argument);
    }

    @FunctionalInterface
    interface Client<T> extends SidedConsumer<T> {

        @Override
        default void accept(T t) {
            if(FMLEnvironment.dist.isClient()) {
                acceptClient(t);
            }
        }

        @OnlyIn(Dist.CLIENT)
        void acceptClient(T t);
    }

    @FunctionalInterface
    interface Server<T> extends SidedConsumer<T> {
        @Override
        default void accept(T t) {
            if(FMLEnvironment.dist.isDedicatedServer()) {
                acceptServer(t);
            }
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        void acceptServer(T t);
    }

    interface Separate<T> extends Consumer<T> {
        @Override
        default void accept(T t) {
            switch(FMLEnvironment.dist) {
                case CLIENT:
                    acceptClient(t);
                    break;
                case DEDICATED_SERVER:
                    acceptServer(t);
                    break;
            }
        }

        @OnlyIn(Dist.CLIENT)
        void acceptClient(T t);

        @OnlyIn(Dist.DEDICATED_SERVER)
        void acceptServer(T t);
    }
}

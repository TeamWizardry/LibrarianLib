package com.teamwizardry.librarianlib.core.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

@FunctionalInterface
public interface SidedRunnable extends Runnable {

    static void client(SidedRunnable.Client runnable) {
        runnable.run();
    }

    static void server(SidedRunnable.Server runnable) {
        runnable.run();
    }

    static void sided(SidedRunnable.Client clientRunnable, SidedRunnable.Server serverRunnable) {
        new Separate() {
            @Override
            public void runClient() {
                clientRunnable.runClient();
            }

            @Override
            public void runServer() {
                serverRunnable.runServer();
            }
        }.run();
    }

    @FunctionalInterface
    interface Client extends SidedRunnable {
        @Override
        default void run() {
            if(FMLEnvironment.dist.isClient()) {
                runClient();
            }
        }

        @OnlyIn(Dist.CLIENT)
        void runClient();
    }

    @FunctionalInterface
    interface Server extends SidedRunnable {
        @Override
        default void run() {
            if(FMLEnvironment.dist.isDedicatedServer()) {
                runServer();
            }
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        void runServer();
    }

    interface Separate extends Runnable {
        @Override
        default void run() {
            switch(FMLEnvironment.dist) {
                case CLIENT:
                    runClient();
                    break;
                case DEDICATED_SERVER:
                    runServer();
                    break;
            }
        }

        @OnlyIn(Dist.CLIENT)
        void runClient();

        @OnlyIn(Dist.DEDICATED_SERVER)
        void runServer();
    }
}

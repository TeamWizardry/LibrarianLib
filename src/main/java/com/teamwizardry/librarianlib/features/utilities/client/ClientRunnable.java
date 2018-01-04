package com.teamwizardry.librarianlib.features.utilities.client;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

/**
 * This class provides a lambda that is safe to contain clientside code.
 * Use this instead of proxying a single use method.
 */
@FunctionalInterface
public interface ClientRunnable {
    /**
     * Executes a lambda only if the side is client.
     * <p>
     * Because the SAM of {@link ClientRunnable} is {@link SideOnly},
     * any {@link SideOnly} client code is safe to have inside.
     *
     * @param runnable A {@link ClientRunnable}.
     */
    static void run(ClientRunnable runnable) {
        LibrarianLib.PROXY.runIfClient(runnable);
    }

    /**
     * Executes a lambda that returns a value, but only if the side is client.
     * <p>
     * Because the SAM of {@link ClientSupplier} is {@link SideOnly},
     * any {@link SideOnly} client code is safe to have inside.
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
            @SideOnly(Side.CLIENT)
            public void runIfClient() {
                holder.setValue(supplier.produceIfClient());
            }
        });
        return holder.getValue();
    }

    static void registerReloadHandler(ClientRunnable runnable) {
        LibrarianLib.PROXY.addReloadHandler(runnable);
    }

    @SideOnly(Side.CLIENT)
    void runIfClient();

    @FunctionalInterface
    interface ClientSupplier<T> {

        @SideOnly(Side.CLIENT)
        T produceIfClient();
    }
}

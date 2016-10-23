package com.teamwizardry.librarianlib.common.util;

import java.lang.invoke.MethodHandle;

/**
 * @author WireSegal
 *         Created at 7:38 PM on 10/22/16.
 */
public class InvocationWrapper {
    private MethodHandle handle;

    public InvocationWrapper(MethodHandle handle) {
        this.handle = handle;
    }

    public Object invoke() throws Throwable {
        return handle.invokeExact();
    }

    public Object invoke(Object obj) throws Throwable {
        return handle.invokeExact(obj);
    }

    public Object invoke(Object obj, Object second) throws Throwable {
        return handle.invokeExact(obj, second);
    }
}

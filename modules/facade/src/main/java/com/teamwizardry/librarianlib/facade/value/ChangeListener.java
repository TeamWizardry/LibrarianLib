package com.teamwizardry.librarianlib.facade.value;

public final class ChangeListener {

    @FunctionalInterface
    public interface Boolean {
        void report(boolean oldValue, boolean newValue);
    }

    @FunctionalInterface
    public interface Double {
        void report(double oldValue, double newValue);
    }

    @FunctionalInterface
    public interface Int {
        void report(int oldValue, int newValue);
    }

    @FunctionalInterface
    public interface Long {
        void report(long oldValue, long newValue);
    }

}

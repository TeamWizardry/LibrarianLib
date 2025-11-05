package com.teamwizardry.librarianlib.facade.value;

@FunctionalInterface
public interface ChangeListener<T> {
    void report(T oldValue, T newValue);

    @FunctionalInterface
    interface Boolean {
        void report(boolean oldValue, boolean newValue);
    }

    @FunctionalInterface
    interface Double {
        void report(double oldValue, double newValue);
    }

    @FunctionalInterface
    interface Int {
        void report(int oldValue, int newValue);
    }

    @FunctionalInterface
    interface Long {
        void report(long oldValue, long newValue);
    }

}

package com.teamwizardry.librarianlib.features.particlesystem.bindings;

import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding;
import org.jetbrains.annotations.NotNull;

public class CallbackBinding implements ReadParticleBinding {
	private final double[] contents;
	private final Callback callback;

	public CallbackBinding(int size, Callback callback) {
		this.contents = new double[size];
		this.callback = callback;
	}

	@Override
	public void load(@NotNull double[] particle) {
		callback.call(particle, contents);
	}

	@Override
	public int getSize() {
		return contents.length;
	}

	@Override
	public double getValue(int index) {
		return contents[index];
	}

	@Override
	public void setValue(int index, double value) {
	}

	@NotNull
	@Override
	public double[] getValues() {
		return contents;
	}

	@FunctionalInterface
	public interface Callback {
		void call(@NotNull double[] particle, @NotNull double[] contents);
	}
}

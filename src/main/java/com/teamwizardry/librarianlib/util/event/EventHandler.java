package com.teamwizardry.librarianlib.util.event;

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {
	void invoke(E event);
}

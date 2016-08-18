package com.teamwizardry.librarianlib.util.javainterfaces;

import com.teamwizardry.librarianlib.util.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {
	void invoke(@NotNull E event);
}

package com.teamwizardry.librarianlib.common.util.lambdainterfs;

import com.teamwizardry.librarianlib.common.util.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Created by TheCodeWarrior
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {
	void invoke(@NotNull E event);
}

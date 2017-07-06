package com.teamwizardry.librarianlib.features.saving;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author TheCodeWarrior
 *
 * Apply this to types to make them dynamically serialized. Meaning that runtime types are preserved. Beware that any
 * runtime types without a serializer, despite the declared type having a serializer, will crash.
 *
 * In addition, [Dyn] does not work on generic types, as the generic type information is lost at runtime, and it is
 * impossible (or at least prohibitively difficult) to infer it from the compile type
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface Dyn {}

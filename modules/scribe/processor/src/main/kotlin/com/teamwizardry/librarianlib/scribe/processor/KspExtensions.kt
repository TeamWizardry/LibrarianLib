package com.teamwizardry.librarianlib.scribe.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

fun Sequence<KSAnnotation>.filterByType(qualifiedName: String): Sequence<KSAnnotation> = this
    .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == qualifiedName }

fun Sequence<KSAnnotation>.filterByType(annotationClass: KClass<*>): Sequence<KSAnnotation> =
    annotationClass.qualifiedName?.let(::filterByType) ?: emptySequence()

inline fun <reified T : Any> Sequence<KSAnnotation>.filterByType(): Sequence<KSAnnotation> =
    filterByType(T::class)

fun Sequence<KSAnnotation>.findByType(qualifiedName: String): KSAnnotation? =
    filterByType(qualifiedName).firstOrNull()

fun Sequence<KSAnnotation>.findByType(annotationClass: KClass<*>): KSAnnotation? =
    filterByType(annotationClass).firstOrNull()

inline fun <reified T : Any> Sequence<KSAnnotation>.findByType(): KSAnnotation? =
    filterByType(T::class).firstOrNull()

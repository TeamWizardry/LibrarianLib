package com.teamwizardry.librarianlib.common.util.autoregister


@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class TileRegister(/** the name or resourcelocation to register as */ val value: String)

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class PartRegister(/** the name or resourcelocation to register as */ val value: String)

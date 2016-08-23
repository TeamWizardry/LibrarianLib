package com.teamwizardry.librarianlib.common.base

/**
 * Provides a list of variants to load. The most basic class which can be put into the ModelHandler.
 */
interface IVariantHolder {

    /**
     * The variants which will be loaded as models. In all implementations these should correspond to metadata values of items.
     * If the mesh definition is not null, none of these will be loaded as models. It's recommended in such situations to use IExtraVariantHolder.
     */
    val variants: Array<out String>
}

/**
 * Provides an extra list of variants to load, without adding extra metadata variants.
 */
interface IExtraVariantHolder : IVariantHolder {

    /**
     * Extra variants to load as models without corresponding to metadata.
     */
    val extraVariants: Array<out String>
}

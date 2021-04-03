package com.teamwizardry.librarianlib.testbase.objects

import net.minecraftforge.common.extensions.IForgeItem

public interface ITestItem: IForgeItem {
    public val itemName: String
    public val itemDescription: String?
}
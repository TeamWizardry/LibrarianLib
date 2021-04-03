package com.teamwizardry.librarianlib.testcore.objects

import net.minecraftforge.common.extensions.IForgeItem

public interface ITestItem: IForgeItem {
    public val itemName: String
    public val itemDescription: String?
}
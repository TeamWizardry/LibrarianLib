package com.teamwizardry.librarianlib.test.saving;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Dyn;
import com.teamwizardry.librarianlib.features.saving.Save;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Document file BlockDynamicSavingTile
 * <p>
 * Created by TheCodeWarrior
 */
@TileRegister("saving_dynamic")
public class BlockDynamicSavingTile extends TileMod {
	@Save
	public List<@Dyn TestType> foo = new ArrayList<>();
}
